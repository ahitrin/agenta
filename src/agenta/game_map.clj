(ns agenta.game-map
  (:import (agenta SingleRandom Unit)))

(defrecord GameMap [size-x size-y cells objs])

(defn- new-map [^GameMap m objs-fn]
  (GameMap. (:size-x m) (:size-y m) (:cells m) (-> (:objs m) objs-fn)))

(defn- rnd-xy!
  "Makes a random pair of coordinates within given boundaries"
  [^SingleRandom r mx my]
  (vec [(.nextInt r mx) (.nextInt r my)]))

(defn plane
  "Fill given rectangle with grass solely"
  [^SingleRandom r size-x size-y]
  {})

(defn forest
  "Fill given rectangle with a mix of grass (95%) and trees (5%)"
  [^SingleRandom r size-x size-y]
  (let [n (int (/ (* size-x size-y) 20))
        trees (take n (distinct (repeatedly #(rnd-xy! r size-x size-y))))]
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
  [^SingleRandom r map-spec units]
  (let [size-x (:size-x map-spec)
        size-y (:size-y map-spec)
        type (resolve (:type map-spec))
        m (GameMap. size-x size-y (apply type [r size-x size-y]) {})
        xys (filter #(can-place? m %)
                    (distinct (repeatedly #(rnd-xy! r size-x size-y))))
        objs (zipmap xys units)]
    (doseq [[[x y] unit] objs]
      (.moveTo unit x y))
    (new-map m #(into % objs))))

(defn try-move! [m ^Unit actor dx dy]
  (let [x (.getX actor) y (.getY actor) nx (+ x dx) ny (+ y dy)]
    (if (can-place? m (list nx ny))
      (do
        (.moveTo actor nx ny)
        (set! (.-speedCounter actor) (.getSpeed (.getType actor)))
        (new-map m #(assoc (dissoc % [x y]) [nx ny] actor)))
      m)))

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

(defn remove-object [m x y]
  (new-map m #(dissoc % [x y])))
