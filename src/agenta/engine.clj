(ns agenta.engine
  (:require [agenta.counter :as ctr]
            [agenta.game-map :as gm]
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

(defn pretty [unit]
  (-> unit
      (assoc :hp (format "%d/%d" (:health unit) (:max-health unit)))
      (select-keys [:kills :name :state :id :hp])))

(defn update-state [actor hp max-hp]
  (let [escape-threshold (int (/ max-hp 5))
        attack-threshold (int (/ max-hp 4))
        old-state (:state actor)
        new-state (cond
                    (< hp escape-threshold) :escape
                    (>= hp attack-threshold) :attack
                    :else old-state)]
    (assoc actor :state new-state)))

(defn normalize-length [[x y]]
  (let [r (Math/sqrt (+ (* x x) (* y y)))]
    [(/ x r) (/ y r)]))

(defn vec+ [v1 v2]
  [(+ (first v1) (first v2)) (+ (second v1) (second v2))])

(defn sign [f]
  "Return sign of a given number (1, -1, 0)"
  (int (Math/signum (float f))))

(defn friends? [u u']
  (= (:player u) (:player u')))

(defn act! [x y actor visible-objects]
  (let [enemies (filter #(not (friends? actor (second %))) visible-objects)
        closest-enemies (filter #(gm/in-radius? [x y] (:range actor) (first %)) enemies)]
    (case (:state actor)
      :escape
      (let [vectors (map #(vec [(- x (first (first %)))
                                (- y (second (first %)))]) enemies)
            norm-vecs (map normalize-length vectors)
            total (if (seq norm-vecs) (reduce vec+ norm-vecs) [0 0])]
        {:type :move :dx (sign (first total)) :dy (sign (second total))})
      :attack
      (cond
        ; attack achievable enemy
        (seq closest-enemies)
        (let [chosen (apply (:select-perk actor) [(map second closest-enemies)])
              ids (clojure.string/join "," (map #(str (:id (second %))) closest-enemies))]
          {:type :attack :target (:id chosen) :ids ids})
        ; approach to enemy
        (seq enemies)
        (let [enemies-without-xy (map second enemies)
              chosen-enemy (apply (:select-perk actor) [enemies-without-xy])
              chosen-idx (.indexOf enemies-without-xy chosen-enemy)
              enemy-with-xy (nth enemies chosen-idx)
              dx (sign (- (first (first enemy-with-xy)) x))
              dy (sign (- (second (first enemy-with-xy)) y))]
          {:type :move :dx dx :dy dy})
        ; random move
        :else
        (apply (:move-perk actor) [x y actor visible-objects])))))

(defn unit-action! [[xy u] m]
  (let [x (:x xy)
        y (:y xy)
        max-hp (:max-health u)
        new-hp (:health u)]
    (if (ctr/ready? (:think-counter u))
      (let [u1 (update-state (update u :think-counter ctr/reset) new-hp max-hp)
            visible-objects (gm/objects-in-radius m x y (:visibility u))
            action (act! x y u1 visible-objects)]
        (log/debugf "%s wants %s" (pretty u1) action)
        [u1 action [x y]])
      [u nil [x y]])))

(defn perform-attack! [m actor action [x y]]
  (let [target-id (int (:target action))
        obj (gm/obj-by-id m target-id)
        u (if (some? obj) (val obj) {})
        my-xy (rnd/xy x y)
        target-xy (if (some? obj) (key obj) (rnd/xy 0 0))]
    (if (and
          (ctr/ready? (:attack-counter actor))
          (some? obj))
      (let [damage (+ (rnd/i! (:rnd-attack actor)) (:base-attack actor))
            hp (:health u)
            new-hp (- hp damage)]
        (if (and (pos? damage) (pos? hp))
          (do
            (log/debugf "%s strikes %s with %d" (pretty actor) (pretty u) damage)
            (let [m1 (gm/new-map m #(update-in % [my-xy :attack-counter] ctr/reset))
                  m2 (gm/new-map m1 #(update-in % [target-xy :health] - damage))
                  u1 (update-in u [:health] - damage)]
              (if-not (pos-int? new-hp)
                (do
                  (log/debugf "%s is dead" (pretty u1))
                  (-> m2
                      (gm/new-map #(update-in % [my-xy :kills] inc))
                      (gm/new-map #(dissoc % target-xy))))
                m2)))
          m))
      m)))

(defn perform-move [m actor action [x y]]
  (let [dx (int (:dx action))
        dy (int (:dy action))]
    (if (ctr/ready? (:speed-counter actor))
      (let [nx (+ x dx) ny (+ y dy) xy (rnd/xy nx ny)]
        (if (gm/can-place? m xy)
          (do
            (let [actor1 (update actor :speed-counter ctr/reset)]
              (gm/new-map m #(assoc (dissoc % (rnd/xy x y)) (rnd/xy nx ny) actor1))))
          m))
      m)))

(def action-selector
  {:attack perform-attack!
   :move   perform-move})

(defn apply-action! [m [actor action [x y]]]
  (let [obj (gm/obj-by-id m (:id actor))
        atype (:type action)]
    (if (and (some? obj)
             ; probably, an excessive check: actors with neg health are removed from map
             (pos? (:health actor)))
      (apply (action-selector atype) [m actor action [x y]])
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
