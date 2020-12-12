(ns agenta.perk
  (:import (agenta SingleRandom)
           (java.util.function Function)))

(defn select-random
  "Given several units select one of them absolutely randomly"
  [^SingleRandom r]
  (proxy [Function] []
    (apply [units]
      (.get units (.nextInt r (count units))))))

(defn select-random-memoized
  "Remember previously selected unit and try to select it when possible; else do it randomly"
  [^SingleRandom r]
  (let [target (atom nil)
        choose (fn [cur new-units]
                 (cond
                   (and (some? cur) (.isAlive cur) (.contains new-units cur)) cur
                   (false? (.isEmpty new-units)) (.get new-units (.nextInt r (count new-units)))
                   :else nil))]
    (proxy [Function] []
      (apply [units]
        (swap! target choose units)))))

(defn select-weakest
  "Given several units select the one who has fewer hit points"
  [^SingleRandom r]
  (proxy [Function] []
    (apply [units]
      (first (sort-by #(.getHitPoints %) units)))))

(def perks {
            :select-random-unit      select-random
            :select-random-unit-memo select-random-memoized
            :select-weakest-unit     select-weakest
            })
