(ns agenta.counter
  (:import [agenta.domain Counter]))

(defn make
  ([max_value] (make max_value max_value))
  ([init_value max_value] (Counter. init_value max_value)))

(defn ready? [counter]
  (.isReady counter))

(defn tick! [counter]
  (.tick counter))

(defn reset [counter]
  (.reset counter)
  counter)
