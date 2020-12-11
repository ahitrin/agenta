(ns agenta.baseline
  (:require agenta.experiment))

; Baseline experiment: each of two players with same forces should have ~50% chance to win

(defn -main [& args]
  (let [s (clojure.edn/read-string (slurp "setting/baseline.edn"))
        results (agenta.experiment/run-experiment 100 5000 s)]
    (agenta.experiment/calc-statistics results)))
