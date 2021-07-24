(ns agenta.game-map
  (:require [agenta.random :as rnd]))

(defrecord GameMap [size-x size-y cells objs])

(defn new-map [^GameMap m objs-fn]
  (GameMap. (:size-x m) (:size-y m) (:cells m) (-> (:objs m) objs-fn)))

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

(defn- object-at [m x y]
  ((:objs m) [x y]))

(defn cell-type [m x y]
  (get (:cells m) [x y] :grass))

(defn- can-place? [m [x y]]
  (and (< -1 x (:size-x m))
       (< -1 y (:size-y m))
       (= :grass (cell-type m x y))
       (nil? (object-at m x y))))

(defn make-map
  "Build game map with given map spec"
  [map-spec units]
  (let [size-x (:size-x map-spec)
        size-y (:size-y map-spec)
        type (resolve (:type map-spec))
        m (GameMap. size-x size-y (apply type [size-x size-y]) {})
        xys (filter #(can-place? m %)
                    (distinct (repeatedly #(rnd/xy! size-x size-y))))
        objs (zipmap xys units)]
    (doseq [[[x y] unit] objs]
      (.moveTo (:old unit) x y))
    (new-map m #(into % objs))))

(defn try-move! [m actor x y dx dy]
  (let [nx (+ x dx) ny (+ y dy)]
    (if (can-place? m (list nx ny))
      (do
        (.moveTo (:old actor) nx ny)
        (set! (.-speedCounter (:old actor)) (.getSpeed (.getType (:old actor))))
        (new-map m #(assoc (dissoc % [x y]) [nx ny] actor)))
      m)))

(defn objects-in-radius [m x y r]
  (let [limit (int r)]
    (filter some?
            (for [i (range (- limit) (inc limit))
                  j (range (- limit) (inc limit))
                  :let [d2 (+ (* i i) (* j j))
                        nx (+ i x)
                        ny (+ j y)]
                  :when (and (<= d2 (* r r))
                             (pos? d2)
                             (< -1 nx (:size-x m))
                             (< -1 ny (:size-y m)))]
              (object-at m nx ny)))))

(defn remove-object [m x y]
  (new-map m #(dissoc % [x y])))
