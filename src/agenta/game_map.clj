(ns agenta.game-map
  (:import (agenta MapCell MapCellType SingleRandom Unit)))

(defrecord GameMap [size-x size-y cells])

(defn rnd-xy
  "Makes a random pair of coordinates within given boundaries"
  [^SingleRandom r mx my]
  (vec [(.nextInt r mx) (.nextInt r my)]))

(defn plane
  "Fill given rectangle with grass solely"
  [^SingleRandom r size-x size-y]
  (let [a (make-array MapCell size-x size-y)]
    (dotimes [x size-x]
      (dotimes [y size-y]
        (aset a x y (MapCell.))))
    a))

(defn forest
  "Fill given rectangle with a mix of grass (95%) and trees (5%)"
  [^SingleRandom r size-x size-y]
  (let [a (plane r size-x size-y)
        n (int (/ (* size-x size-y) 20))
        trees (take n (distinct (repeatedly #(rnd-xy r size-x size-y))))]
    (doseq [[x y] trees] (aset a x y (MapCell. MapCellType/TREE)))
    a))

(defn object-at [m x y]
  (.getObject (aget (.-cells m) x y)))

(defn can-place? [m x y]
  (and (< -1 x (:size-x m))
       (< -1 y (:size-y m))
       (= MapCellType/GRASS (.getType (aget (.-cells m) x y)))
       (nil? (object-at m x y))))

(defn place-object [m ^Unit u x y]
  (.setObject (aget (.-cells m) x y) u)
  (.moveTo u x y))

(defn make-map
  "Build game map with given map spec"
  [^SingleRandom r map-spec units]
  (let [size-x (:size-x map-spec)
        size-y (:size-y map-spec)
        type (resolve (:type map-spec))
        m (GameMap. size-x size-y (apply type [r size-x size-y]))]
    (doseq [unit units]
      (let [xy (first (filter #(can-place? m (first %) (second %))
                              (repeatedly #(rnd-xy r size-x size-y))))]
        (place-object m unit (first xy) (second xy))))
    m))

(defn try-move [m ^Unit actor dx dy]
  (let [x (.getX actor) y (.getY actor) nx (+ x dx) ny (+ y dy)]
    (if (can-place? m nx ny)
      (do
        (.setObject (aget (.-cells m) x y) nil)
        (place-object m actor nx ny)
        true)
      false)))

(defn cell-type [m x y]
  (.getType (aget (.-cells m) x y)))

(defn objects-in-radius [m ^Unit u r]
  (let [limit (int r)]
    (filter some?
            (for [i (range (- limit) (inc limit))
                  j (range (- limit) (inc limit))
                  :let [d2 (+ (* i i) (* j j))
                        nx (+ i (.getX u))
                        ny (+ j (.getY u))]
                  :when (and (<= d2 (* r r))
                             (pos? d2)
                             (< -1 nx (:size-x m))
                             (< -1 ny (:size-y m)))]
              (object-at m nx ny)))))

(defn remove-object [m ^Unit u]
  (.setObject (aget (.-cells m) (.getX u) (.getY u)) nil))
