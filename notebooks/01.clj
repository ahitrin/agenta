;; # First empty notebook

(agenta.random/init!)

(def setting (clojure.edn/read-string (slurp "setting/demo.edn")))
(def start-units (agenta.engine/init-units setting))
(def start-map (agenta.game-map/make-map (:map setting) start-units))

;; Wow, we have explorable objects here. Looks nice!
