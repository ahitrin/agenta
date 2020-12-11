(ns agenta.baseline
  (:require agenta.experiment)
  (:import (agenta UnitTypeImpl)))

; Baseline experiment: each of two players with same forces should have ~50% chance to win

(def unit-types
  [(doto
     (UnitTypeImpl.)
     (.setName "Warrior")
     (.setAttackSpeed 3)
     (.setBaseAttack 1)
     (.setRandAttack 4)
     (.setHitPoints 10)
     (.setSpeed 3)
     (.setVisibility 10)
     (.setRange 1.45)
     (.setImage "footman"))])

(defn -main [& args]
  (let [s (clojure.edn/read-string (slurp "setting/baseline.edn"))
        results (agenta.experiment/run-experiment 100 5000 s unit-types)]
    (agenta.experiment/calc-statistics results)))
