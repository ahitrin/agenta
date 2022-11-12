(ns agenta.perk
  (:require [agenta.random :as rnd])
  (:import (java.util.function Function)))

(defn as-java-fn [fun]
  (proxy [Function] [] (apply [arg] (apply fun [arg]))))

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
                   (and (some? cur) (pos? (.-currentHitPoints cur)) (.contains new-units cur)) cur
                   (false? (.isEmpty new-units)) (.get new-units (rnd/i! (count new-units)))
                   :else nil))]
    (swap! target choose units)))

(defn select-weakest
  "Given several units select the one who has fewer hit points"
  [units]
  (first (sort-by #(.-currentHitPoints %) units)))
