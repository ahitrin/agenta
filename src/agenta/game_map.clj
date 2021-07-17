(ns agenta.game-map
  (:import (agenta MapCell MapCellType SingleRandom Unit)))

(defrecord GameMap [size-x size-y cells objs])

(defn -rnd-xy!
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
        trees (take n (distinct (repeatedly #(-rnd-xy! r size-x size-y))))]
    (doseq [[x y] trees] (aset a x y (MapCell. MapCellType/TREE)))
    a))

(defn -object-at [m x y]
  ((:objs m) [x y]))

(defn -can-place? [m [x y]]
  (and (< -1 x (:size-x m))
       (< -1 y (:size-y m))
       (= MapCellType/GRASS (.getType (aget (:cells m) x y)))
       (nil? (-object-at m x y))))

(defn make-map
  "Build game map with given map spec"
  [^SingleRandom r map-spec units]
  (let [size-x (:size-x map-spec)
        size-y (:size-y map-spec)
        type (resolve (:type map-spec))
        m (GameMap. size-x size-y (apply type [r size-x size-y]) {})
        xys (filter #(-can-place? m %)
                    (distinct (repeatedly #(-rnd-xy! r size-x size-y))))
        objs (zipmap xys units)]
    (doseq [[[x y] unit] objs]
      (.moveTo unit x y))
    (GameMap. size-x size-y (:cells m) objs)))

(defn try-move! [m ^Unit actor dx dy]
  (let [x (.getX actor) y (.getY actor) nx (+ x dx) ny (+ y dy)]
    (if (-can-place? m (list nx ny))
      (do
        (.moveTo actor nx ny)
        (set! (.-speedCounter actor) (.getSpeed (.getType actor)))
        (GameMap. (:size-x m) (:size-y m) (:cells m) (-> (:objs m)
                                                         (dissoc [x y])
                                                         (assoc [nx ny] actor))))
      m)))

(defn cell-type [m x y]
  (.getType (aget (:cells m) x y)))

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
              (-object-at m nx ny)))))

(defn remove-object! [m ^Unit u]
  (GameMap. (:size-x m) (:size-y m) (:cells m) (dissoc (:objs m) [(.getX u) (.getY u)])))
