(ns agenta.game-map
  (:import (agenta GameMap MapCell MapCellType SingleRandom)))

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

(def types {
            :plane plane
            :forest forest
            })

(defn make-map
  "Build game map with given map spec"
  [^SingleRandom r map-spec]
  (let [size-x (:size-x map-spec)
        size-y (:size-y map-spec)
        type (types (:type map-spec))]
    (GameMap. (apply type [r size-x size-y]))))
