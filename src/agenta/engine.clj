(ns agenta.engine
  (:require [agenta.counter :as ctr]
            [agenta.game-map :as gm]
            [agenta.perk :as pk]
            [agenta.random :as rnd]
            [clojure.tools.logging :as log])
  (:import (com.github.javafaker Faker)))

(defn make-unit [random unit-type]
  "Create one unit dictionary from given specs"
  {
   ; "static" properties (do not change during game)
   :max-spd        (:speed unit-type)
   :max-health     (:hitPoints unit-type)
   :visibility     (:visibility unit-type)
   :base-attack    (:baseAttack unit-type)
   :rnd-attack     (:randAttack unit-type)
   :range          (:range unit-type)
   :select-perk    (resolve (.get (:perk unit-type) :select))
   :img            (str (:image unit-type) (:player unit-type))
   :id             (:id unit-type)
   :player         (:player unit-type)
   :name           (format "%s %s%d"
                           (.firstName (.name (Faker.)))
                           (:name unit-type)
                           (:player unit-type))
   ; "dynamic" properties (change during game)
   :attack-counter (ctr/make (inc (rnd/i! (:attackSpeed unit-type))) (:attackSpeed unit-type))
   :health-counter (ctr/make (inc (rnd/i! 100)) 100)
   :speed-counter  (ctr/make (inc (rnd/i! (:speed unit-type))) (:speed unit-type))
   :think-counter  (ctr/make (inc (rnd/i! 3)) 3)
   :health         (:hitPoints unit-type)
   :state          :attack
   :kills          0})


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
        g (rnd/get-generator)
        defs+ (map-indexed #(assoc %2 :id %) defs)]
    (map #(make-unit g %) defs+)))

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

(defn wrap-act! [m x y actor visible-objects]
  "Temporary replacement for Unit.act"
  (let [state (:state actor)
        enemies (filter #(not= (:player (second %)) (:player actor)) visible-objects)]
    (case state
      :escape
      (let [vectors (map #(vec [(- x (first (first %)))
                                (- y (second (first %)))]) enemies)
            norm-vecs (map normalize-length vectors)
            total (if (seq norm-vecs) (reduce vec+ norm-vecs) [0 0])]
        {:type :move :dx (sign (first total)) :dy (sign (second total))})
      :attack
      (let [closest-enemies (filter #(gm/in-radius? [x y] (:range actor) (first %)) enemies)]
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
          (let [dx (dec (rnd/i! 3)) dy (dec (rnd/i! 3))]
            {:type :move :dx dx :dy dy}))))))

(defn run-unit-action! [[[x y] u] m]
  (let [max-hp (:max-health u)
        new-hp (:health u)
        visible-objects (gm/objects-in-radius m x y (:visibility u))]
    (if (ctr/ready? (:think-counter u))
      (let [u1 (update-state (update u :think-counter ctr/reset) new-hp max-hp)
            action (wrap-act! m x y u1 visible-objects)]
        (log/debugf "%s wants %s" (pretty u1) action)
        [u1 action [x y]])
      [u nil [x y]])))

(defn perform-attack! [m actor action [x y]]
  (let [target-id (int (:target action))
        obj (gm/obj-by-id m target-id)
        u (if (some? obj) (val obj) {})
        [tx ty] (if (some? obj) (key obj) [0 0])]
    (if (and
          (ctr/ready? (:attack-counter actor))
          (some? obj))
      (let [damage (+ (rnd/i! (:rnd-attack actor)) (:base-attack actor))
            hp (:health u)
            new-hp (- hp damage)]
        (if (and (pos? damage) (pos? hp))
          (do
            (log/debugf "%s strikes %s with %d" (pretty actor) (pretty u) damage)
            (let [m1 (gm/new-map m #(update-in % [[x y] :attack-counter] ctr/reset))
                  m2 (gm/new-map m1 #(update-in % [[tx ty] :health] - damage))
                  u1 (update-in u [:health] - damage)]
              (if-not (pos-int? new-hp)
                (do
                  (log/debugf "%s is dead" (pretty u1))
                  (-> m2
                      (gm/new-map #(update-in % [[x y] :kills] inc))
                      (gm/new-map #(dissoc % [tx ty]))))
                m2)))
          m))
      m)))

(defn perform-move [m actor action [x y]]
  (let [dx (int (:dx action))
        dy (int (:dy action))]
    (if (ctr/ready? (:speed-counter actor))
      (let [nx (+ x dx) ny (+ y dy)]
        (if (gm/can-place? m [nx ny])
          (do
            (let [actor1 (update actor :speed-counter ctr/reset)]
              (gm/new-map m #(assoc (dissoc % [x y]) [nx ny] actor1))))
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
            objs (:objs m1)]
        (viewer m1 objs)
        (if (or (<= 0 winner) (<= limit tick))
          {:winner winner :steps tick}
          (let [actions (set (filter #(some? (second %)) (map #(run-unit-action! % m1) objs)))
                new-m (apply-actions! actions m1)
                units-per-player (group-by :player (vals (:objs new-m)))]
            (recur new-m
                   (cond (empty? (units-per-player 0)) 1
                         (empty? (units-per-player 1)) 0
                         :else -1)
                   (inc tick))))))))
