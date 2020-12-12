(ns agenta.perk
  (:import (agenta SingleRandom Selector)))

(defn select-random-memoized
  [^SingleRandom r]
  (let [target (atom nil)
        choose (fn [cur new-units]
                 (cond
                   (and (some? cur) (.isAlive cur) (.contains new-units cur)) cur
                   (false? (.isEmpty new-units)) (.get new-units (.nextInt r (count new-units)))
                   :else nil))]
    (proxy [Selector] []
      (apply [units]
        (swap! target choose units)))))
