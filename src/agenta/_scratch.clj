;; supplementary REPL functions

(comment

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
