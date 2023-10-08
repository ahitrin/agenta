(ns agenta.math
  "Basic math functions and data structures")

; Two coordinates on a game map
(defrecord XY [^long x ^long y])

(defn xy [x y] (XY. x y))

; 2d vector (directed distance) between two XY points
(defrecord V [dx dy])

(defn v [dx dy] (V. dx dy))

(defn v-> [^XY xy ^XY xy']
  "Create a vector from xy to xy'"
  (V. (- (:x xy') (:x xy)) (- (:y xy') (:y xy))))

(defn xy+v [^XY xy ^V v]
  "Add vector v to the given point"
  (XY. (+ (:x xy) (:dx v)) (+ (:y xy) (:dy v))))

(defn v+v [^V v ^V v']
  "Add two vectors"
  (V. (+ (:dx v) (:dx v')) (+ (:dy v) (:dy v'))))

(defn v* [mult ^V v]
  "Multiply a vector to the given multiplier"
  (V. (* mult (:dx v)) (* mult (:dy v))))

(defn sqr [x] (* x x))

(defn len [^V v]
  "Length of a given 2d vector"
  (let [dx (:dx v) dy (:dy v)]
    (Math/sqrt (+ (* dx dx) (* dy dy)))))

(defn len2
  "Squared length of a given 2d vector"
  ([^long dx ^long dy] (+ (sqr dx) (sqr dy)))
  ([^XY xy] (len2 (:x xy) (:y xy))))

(defn in-radius?
  "Check whether 2d vector [x y]->[nx ny] has length less or equal to r"
  ([^double r [^long dx ^long dy]] (<= (len2 dx dy) (sqr r)))
  ([^XY xy ^double r ^XY nxy] (in-radius? r [(- (:x xy) (:x nxy)) (- (:y xy) (:y nxy))])))

(defn- circle-of-keys-raw [^double r]
  (let [limit (int r)]
    (for [i (range (- limit) (inc limit))
          j (range (- limit) (inc limit))
          :when (and (in-radius? r [i j])
                     (not= 0 i j))]
      [i j])))

(def circle-of-keys (memoize circle-of-keys-raw))

(defn normalize-length [^V v]
  "Transform 2d vector into vector of the same direction and length 1"
  (v* (/ 1 (len v)) v))

(defn sign [f]
  "Return sign of a given number (1, -1, 0)"
  (int (Math/signum (float f))))
