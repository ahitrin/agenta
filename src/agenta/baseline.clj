(ns agenta.baseline
  (:require agenta.experiment)
  (:import (agenta UnitTypeImpl)
           (agenta.experiment Experiment)))

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
     (.setRange 1.45))])

(defn -main [& args]
  (let [player-units {"warrior" 10}
        results (agenta.experiment/run-experiment 100 5000 unit-types player-units player-units)]
    (Experiment/calculateStatistics results)))
