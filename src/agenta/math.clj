(ns agenta.math
  "Basic math functions and data structures")

; Two coordinates on a game map
(defrecord XY [^long x ^long y])

(defn xy [x y] (XY. x y))
