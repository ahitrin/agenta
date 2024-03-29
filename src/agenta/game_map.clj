(ns agenta.game-map
  (:require [agenta.math :as m]
            [agenta.random :as rnd]
            [agenta.unit :as u]))

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

(defn object-at [m xy]
  ((:objs m) xy))

(defn cell-type [m xy]
  (get (:cells m) xy :grass))

(defn can-place? [m xy]
  (and (< -1 (:x xy) (:size-x m))
       (< -1 (:y xy) (:size-y m))
       (= :grass (cell-type m xy))
       (nil? (object-at m xy))))

(defn choose-xys
  "Choose locations for units respecting to the free space and location rules."
  [^GameMap m setting units]
  (let [def-loc  [0 (:size-x m) 0 (:size-y m)]
        locs     (into {}
                       (for [p  (range 2)
                             ut (get (:placement setting) p)
                             :let [loc (get (val ut) :location def-loc)]]
                         [[p (key ut)] loc]))
        free     (into #{}
                       (for [x (range (:size-x m))
                             y (range (:size-y m))
                             :let [p (m/xy x y)]
                             :when (can-place? m p)]
                         p))]
    (loop [placed {} not-placed units free-cells free]
      (if (empty? not-placed)
        placed
        (let [next-unit (first not-placed)
              loc-key   [(:player next-unit) (:type next-unit)]
              next-xy   (->> (repeatedly #(apply rnd/xy! (get locs loc-key)))
                             (filter #(contains? free-cells %))
                             first)]
          (recur (assoc placed next-xy next-unit)
                 (rest not-placed)
                 (disj free-cells next-xy)))))))

(defn make-map
  "Build game map from given setting."
  [setting]
  (let [map-spec    (:map setting)
        size-x      (:size-x map-spec)
        size-y      (:size-y map-spec)
        map-type    (resolve (:type map-spec))
        m           (GameMap. size-x size-y (apply map-type [size-x size-y]) {} {})
        units       (u/init-units setting)
        objs        (choose-xys m setting units)]
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
      [nxy (object-at m nxy)])))
