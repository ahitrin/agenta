(ns agenta.math
  "Basic math functions and data structures")

; Two coordinates on a game map
(defrecord XY [^long x ^long y])

(defn xy [x y] (XY. x y))

(defn sqr [x] (* x x))

(defn len2
  "Squared length of a given 2d vector"
  ([^long dx ^long dy] (+ (sqr dx) (sqr dy)))
  ([^XY xy] (len2 (:x xy) (:y xy))))

(defn in-radius?
  "Check whether 2d vector [x y]->[nx ny] has length less or equal to r"
  ([^double r [^long dx ^long dy]] (<= (len2 dx dy) (sqr r)))
  ([[^long x ^long y] ^double r [^long nx ^long ny]] (in-radius? r [(- x nx) (- y ny)])))

(defn- circle-of-keys-raw [^double r]
  (let [limit (int r)]
    (for [i (range (- limit) (inc limit))
          j (range (- limit) (inc limit))
          :when (and (in-radius? r [i j])
                     (not= 0 i j))]
      [i j])))

(def circle-of-keys (memoize circle-of-keys-raw))
