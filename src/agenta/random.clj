(ns agenta.random
  (:require [clojure.tools.logging :as log]
            [agenta.math :as m])
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

(defn xy!
  "Makes a random pair of coordinates within given boundaries"
  [mx my]
  (m/xy (.nextInt generator mx) (.nextInt generator my)))

(defn i!
  "Makes a random integer between 0 (inclusive) and n (exclusive)"
  [n]
  (.nextInt generator n))
