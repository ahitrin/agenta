(ns agenta.game-map
  (:require [agenta.math :as m]
            [agenta.random :as rnd]))

(defrecord GameMap [size-x size-y cells objs id-to-xy])

(defn- into-xy [old-obj]
  (hash-map (:id (second old-obj)) (first old-obj)))

(defn new-map [^GameMap m objs-fn]
  (let [objs (-> (:objs m) objs-fn)
        idx  (reduce merge (map into-xy objs))]
    (GameMap. (:size-x m) (:size-y m) (:cells m) objs idx)))

(defn plane
  "Fill given rectangle with grass solely"
  [_ _]
  {})

(defn forest
  "Fill given rectangle with a mix of grass (95%) and trees (5%)"
  [size-x size-y]
  (let [n (int (/ (* size-x size-y) 20))
        trees (take n (distinct (repeatedly #(rnd/xy! size-x size-y))))]
    (apply assoc {} (interleave trees (repeat n :tree)))))

(defn xy-to-unit [^GameMap m]
  (:objs m))

(defn object-at [m x y]
  ((:objs m) (m/xy x y)))

(defn cell-type [m x y]
  (get (:cells m) (m/xy x y) :grass))

(defn can-place? [m xy]
  (let [x (:x xy) y (:y xy)]
    (and (< -1 x (:size-x m))
         (< -1 y (:size-y m))
         (= :grass (cell-type m x y))
         (nil? (object-at m x y)))))

(defn make-map
  "Build game map with given map spec"
  [map-spec units]
  (let [size-x (:size-x map-spec)
        size-y (:size-y map-spec)
        type (resolve (:type map-spec))
        m (GameMap. size-x size-y (apply type [size-x size-y]) {} {})
        xys (filter #(can-place? m %)
                    (distinct (repeatedly #(rnd/xy! size-x size-y))))
        objs (zipmap xys units)]
    (new-map m #(into % objs))))

(defn obj-by-id [m oid]
  (find (:objs m) (get (:id-to-xy m) oid)))

(defn objects-in-radius [m oid ^double r]
  (let [ks (m/circle-of-keys r)
        xy (get (:id-to-xy m) oid)]
    (for [[dx dy] ks
          :let [nx (+ (:x xy) dx)
                ny (+ (:y xy) dy)
                nxy (m/xy nx ny)]
          :when (and (< -1 nx (:size-x m))
                     (< -1 ny (:size-y m))
                     (contains? (:objs m) nxy))]
      [nxy (object-at m nx ny)])))
