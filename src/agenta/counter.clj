(ns agenta.counter)

(defn make
  ([max_value] (make max_value max_value))
  ([init_value max_value] [init_value max_value]))

(defn ready? [counter]
  (zero? (first counter)))

(defn tick [counter]
  (make (max (dec (first counter)) 0) (second counter)))

(defn reset [counter]
  (make (second counter) (second counter)))
