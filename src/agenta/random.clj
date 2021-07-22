(ns agenta.random
  (:require [clojure.tools.logging :as log])
  (:import (agenta SingleRandom)
           (java.util Random)))

(def generator nil)

(defn init! []
  (alter-var-root
    (var generator)
    (fn [_]
      (let [seed (.nextLong (Random.))]
        (log/infof "Seed: %d" seed)
        (SingleRandom. (Random. seed))))))

(defn get-generator []
  generator)

(defn rnd-xy!
  "Makes a random pair of coordinates within given boundaries"
  [mx my]
  (vec [(.nextInt generator mx) (.nextInt generator my)]))
