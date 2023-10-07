;; supplementary REPL functions

(comment
  ;; reload modules after changes
  (use 'agenta.engine :reload)
  (use 'agenta.game-map :reload)
  (use 'agenta.math :reload)
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

  (prof/profile (dotimes [_ 100000]
    (let [x (agenta.game-map/objects-in-radius start-map 10 5.0)
          y (agenta.game-map/objects-in-radius' start-map 10 5.0)]
        (= x y))))

  (prof/serve-ui 8080)

  ;; various
  )
