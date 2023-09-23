;; supplementary REPL functions

(comment
  ;; reload modules after changes
  (use 'agenta.engine :reload)
  (use 'agenta.game-map :reload)
  (use 'agenta.random :reload)

  ;; init randomizer before start
  (agenta.random/init!)

  ;; init game map and unit list
  (def setting (clojure.edn/read-string (slurp "setting/demo.edn")))
  (def start-units (agenta.engine/init-units setting))
  (def start-map (agenta.game-map/make-map (:map setting) start-units))

  ;; profiling
  (require '[clj-async-profiler.core :as prof])

  (prof/profile
    (agenta.engine/run-game! setting (fn [m u] ())))

  ; looks like records perform faster than vectors in comparison
  (defrecord XY [x y])
  (prof/profile (dotimes [_ 1000000] (= [1 2] [1 2]) (= (XY. 1 2) (XY. 1 2))))

  ; what's faster: ask a map N times for a key or merge keyset with N-size set?
  (defn find-by-contains [m x y r]
    (for [i (range (- r) (inc r))
          j (range (- r) (inc r))
          :let [nx (+ i x) ny (+ j y)]
          :when (and (agenta.game-map/in-radius? r [i j])
                     (contains? m (agenta.random/xy nx ny)))]
      [nx ny]))

  (defn find-by-intersection [m x y r]
    (let [ks (for [i (range (- r) (inc r))
                   j (range (- r) (inc r))
                   :let [nx (+ i x) ny (+ j y)]
                   :when (agenta.game-map/in-radius? r [i j])]
                (agenta.random/xy nx ny))]
        (clojure.set/intersection (set ks) (set (keys m)))))

  (let [m (zipmap
            (take 10 (repeatedly #(agenta.random/xy! 20 20)))
            (repeat 1))
        x 10
        y 10
        r 7]
    (prof/profile (dotimes [_ 1000000]
                    (find-by-contains m x y r)
                    (find-by-intersection m x y r)
                    )))


  (prof/serve-ui 8080)

  ;; various
  (def u {:kills 0,
          :speed-counter [19 40],
          :name "Kevin Archer0",
          :max-health 55,
          :attack-counter [31 40],
          :id 21,
          :health 55,
          :max-spd 40,
          :health-counter [8 100],
          :visibility 7,
          :img "archer0"})


  )
