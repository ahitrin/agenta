(ns agenta.perk
  (:require [agenta.random :as rnd])
  (:import (java.util.function Function)))

(defn select-random
  "Given several units select one of them absolutely randomly"
  []
  (proxy [Function] []
    (apply [units]
      (.get units (rnd/i! (count units))))))

(defn select-random-memoized
  "Remember previously selected unit and try to select it when possible; else do it randomly"
  []
  (let [target (atom nil)
        choose (fn [cur new-units]
                 (cond
                   (and (some? cur) (.isAlive cur) (.contains new-units cur)) cur
                   (false? (.isEmpty new-units)) (.get new-units (rnd/i! (count new-units)))
                   :else nil))]
    (proxy [Function] []
      (apply [units]
        (swap! target choose units)))))

(defn select-weakest
  "Given several units select the one who has fewer hit points"
  []
  (proxy [Function] []
    (apply [units]
      (first (sort-by #(.-currentHitPoints %) units)))))
