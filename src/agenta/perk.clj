(ns agenta.perk
  (:require [agenta.random :as rnd]))

; ---- Select perks ----

(defn select-random
  "Given several units select one of them absolutely randomly"
  [units]
  (.get units (rnd/i! (count units))))

(defn select-random-memoized
  "Remember previously selected unit and try to select it when possible; else do it randomly"
  [units]
  (let [target (atom nil)
        choose (fn [cur new-units]
                 (cond
                   (and (some? cur)
                        (pos? (:health cur))
                        (contains? new-units cur))
                   cur
                   (seq new-units)
                   (.get new-units (rnd/i! (count new-units)))
                   :else nil))]
    (swap! target choose units)))

(defn select-weakest
  "Given several units select the one who has fewer hit points"
  [units]
  (first (sort-by :health units)))

; ---- Move perks ----

(defn move-randomly [x y actor visible-objects]
  "Ignore visible objects and current actor state, always move randomly"
  (let [dx (dec (rnd/i! 3)) dy (dec (rnd/i! 3))]
    {:type :move :dx dx :dy dy}))

(defn move-to-friends [x y actor visible-objects]
  "Move towards the center of friendly units of any type"
  (let [player (:player actor)
        friends (filter #(= player (:player (second %))) visible-objects)
        dx (->> friends (map first) (map :x) (map #(- % x)) (reduce +) float Math/signum int)
        dy (->> friends (map first) (map :y) (map #(- % y)) (reduce +) float Math/signum int)]
    {:type :move :dx dx :dy dy}))
