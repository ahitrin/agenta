(ns agenta.random
  (:require [clojure.tools.logging :as log])
  (:import (java.util Random)))

(def generator nil)

(defn init! []
  (alter-var-root
    (var generator)
    (fn [_]
      (let [seed (.nextLong (Random.))]
        (log/infof "Seed: %d" seed)
        (Random. seed)))))

(defn get-generator
  "Returns an Random instance"
  []
  generator)

; Two coordinates on a game map
(defrecord XY [^long x ^long y])

(defn xy [x y] (XY. x y))

(defn xy!
  "Makes a random pair of coordinates within given boundaries"
  [mx my]
  (xy (.nextInt generator mx) (.nextInt generator my)))

(defn i!
  "Makes a random integer between 0 (inclusive) and n (exclusive)"
  [n]
  (.nextInt generator n))
