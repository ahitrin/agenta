(ns agenta.engine
  (:require [agenta.counter :as ctr]
            [agenta.game-map :as gm]
            [agenta.math :as m]
            [agenta.random :as rnd]
            [agenta.unit :as u]
            [clojure.tools.logging :as log]))

(defn into-defs [setting]
  "Transform setting object into a list of unit defs (specs)"
  (for [ut (:unit-types setting)
        p (range 2)
        _ (range (-> (:placement setting)
                     (get p)
                     (get (.toLowerCase (:name ut)) 0)))]
    (assoc ut :player p)))

(defn init-units [setting]
  "Create all units described by setting"
  (let [defs (into-defs setting)
        defs+ (map-indexed #(assoc %2 :id %) defs)]
    (map u/make-unit defs+)))

(defn choose-enemy [actor enemies]
  (:target (apply (:select-perk actor) actor [(map second enemies)])))

(defn act! [xy actor visible-objects]
  (let [enemies (filter #(not (u/friends? actor (second %))) visible-objects)
        x (:x xy) y (:y xy)
        closest-enemies (filter #(m/in-radius? xy (:range actor) (first %)) enemies)]
    (case (:state actor)
      :escape
      (let [vectors (map #(m/xy (- x (:x (first %)))
                                (- y (:y (first %)))) enemies)
            norm-vecs (map m/normalize-length vectors)
            total (if (seq norm-vecs) (reduce m/vec+ norm-vecs) [0 0])]
        {:type :move :dx (m/sign (first total)) :dy (m/sign (second total))})
      :attack
      (cond
        ; attack achievable enemy
        (seq closest-enemies)
        (let [chosen (choose-enemy actor closest-enemies)
              ids (clojure.string/join "," (map #(str (:id (second %))) closest-enemies))]
          {:type :attack :target (:id chosen) :ids ids})
        ; approach to enemy
        (seq enemies)
        (let [enemies-without-xy (map second enemies)
              chosen-enemy (choose-enemy actor enemies)
              chosen-idx (.indexOf enemies-without-xy chosen-enemy)
              enemy-with-xy (nth enemies chosen-idx)
              dx (m/sign (- (:x (first enemy-with-xy)) x))
              dy (m/sign (- (:y (first enemy-with-xy)) y))]
          {:type :move :dx dx :dy dy})
        ; random move
        :else
        (apply (:move-perk actor) [xy actor visible-objects])))))

(defn unit-action! [[xy u] m]
  (let [max-hp (:max-health u)
        new-hp (:health u)]
    (if (ctr/ready? (:think-counter u))
      (let [u1 (u/update-state (update u :think-counter ctr/reset) new-hp max-hp)
            visible-objects (gm/objects-in-radius m (:id u) (:visibility u))
            action (act! xy u1 visible-objects)]
        (log/debugf "%s wants %s" (u/pretty u1) action)
        [u1 action xy])
      [u nil xy])))

(defn perform-attack! [m actor action xy]
  (let [target-id (int (:target action))
        obj (gm/obj-by-id m target-id)
        u (if (some? obj) (val obj) {})
        target-xy (if (some? obj) (key obj) (m/xy 0 0))]
    (if (and
         (ctr/ready? (:attack-counter actor))
         (some? obj))
      (let [damage (+ (rnd/i! (:rnd-attack actor)) (:base-attack actor))
            hp (:health u)
            new-hp (- hp damage)]
        (if (and (pos? damage) (pos? hp))
          (do
            (log/debugf "%s strikes %s with %d" (u/pretty actor) (u/pretty u) damage)
            (let [m1 (gm/new-map m #(update-in % [xy :attack-counter] ctr/reset))
                  m2 (gm/new-map m1 #(update-in % [target-xy :health] - damage))
                  u1 (update-in u [:health] - damage)]
              (if-not (pos-int? new-hp)
                (do
                  (log/debugf "%s is dead" (u/pretty u1))
                  (-> m2
                      (gm/new-map #(update-in % [xy :kills] inc))
                      (gm/new-map #(dissoc % target-xy))))
                m2)))
          m))
      m)))

(defn perform-move [m actor action xy]
  (let [dx (int (:dx action))
        dy (int (:dy action))]
    (if (ctr/ready? (:speed-counter actor))
      (let [nx  (+ (:x xy) dx)
            ny  (+ (:y xy) dy)
            xy' (m/xy nx ny)]
        (if (gm/can-place? m xy')
          (do
            (let [actor1 (update actor :speed-counter ctr/reset)]
              (gm/new-map m #(assoc (dissoc % xy) xy' actor1))))
          m))
      m)))

(def action-selector
  {:attack perform-attack!
   :move   perform-move})

(defn apply-action! [m [actor action xy]]
  (let [obj (gm/obj-by-id m (:id actor))
        atype (:type action)]
    (if (and (some? obj)
             ; probably, an excessive check: actors with neg health are removed from map
             (pos? (:health actor)))
      (apply (action-selector atype) [m actor action xy])
      m)))

(defn apply-actions! [actions m]
  (reduce apply-action! m actions))

(defn on-hp-tick [m]
  (let [m1 (-> m
               (update :health-counter ctr/tick)
               (update :speed-counter ctr/tick)
               (update :attack-counter ctr/tick)
               (update :think-counter ctr/tick))
        grow (if (< (:health m1) (:max-health m1)) 1 0)
        m2 (if (ctr/ready? (:health-counter m1))
             (-> m1
                 (update :health-counter ctr/reset)
                 (update :health + grow))
             m1)]
    m2))

(defn tick-health [objs]
  (reduce-kv #(assoc %1 %2 (on-hp-tick %3)) {} objs))

(defn run-game! [setting viewer]
  (let [u (init-units setting)
        start-map (gm/make-map (:map setting) u)
        limit (:max-ticks (:experiment setting))]
    (loop [m start-map winner -1 tick 0]
      (let [m1 (gm/new-map m tick-health)
            objs (gm/xy-to-unit m1)]
        (viewer m1 objs)
        (if (or (<= 0 winner) (<= limit tick))
          {:winner winner :steps tick}
          (let [actions (set (filter #(some? (second %)) (map #(unit-action! % m1) objs)))
                new-m (apply-actions! actions m1)
                units-per-player (group-by :player (vals (gm/xy-to-unit new-m)))]
            (recur new-m
                   (cond (empty? (units-per-player 0)) 1
                         (empty? (units-per-player 1)) 0
                         :else -1)
                   (inc tick))))))))
