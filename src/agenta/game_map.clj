(ns agenta.game-map
  (:import (agenta GameMap MapCell MapCellType SingleRandom Unit)))

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

(defn make-map
  "Build game map with given map spec"
  [^SingleRandom r map-spec]
  (let [size-x (:size-x map-spec)
        size-y (:size-y map-spec)
        type (resolve (:type map-spec))]
    (GameMap. r (apply type [r size-x size-y]))))

(defn size-x [^GameMap m]
  (.-sizeX m))

(defn size-y [^GameMap m]
  (.-sizeY m))

(defn place-where-possible [^GameMap m ^Unit u]
  (.placeWherePossible m u))

(defn try-move [^GameMap m ^Unit actor dx dy]
  (.tryMove m actor dx dy))

(defn cell-type [^GameMap m x y]
  (if (and (< -1 x (.-sizeX m))
           (< -1 y (.-sizeY m)))
   (.getType (aget (.-cells m) x y))
    MapCellType/TREE))

(defn object-at [^GameMap m x y]
  (cast Unit (.getGroundObject m x y)))

(defn objects-in-radius [^GameMap m ^Unit u r]
  (.getObjectsInRadius m u r))

(defn remove-object [^GameMap m ^Unit u]
  (.removeObject m u))
