(ns agenta.unit-test
  (:require [clojure.test :refer :all])
  (:import (agenta Unit UnitType UnitCommand UnitState)
           (java.util Random)))

(def fake-unit-type
      (proxy [UnitType] []
        (getBaseAttack [] 1)
        (getRandAttack [] 2)
        (getRange [] 1)
        (getAttackSpeed [] 1)
        (getHitPoints [] 5)
        (getHealthLimit [lvl] lvl)
        (toString [] "Peasant")))

(deftest default-state
  (testing "unit default state"
    (let [ut fake-unit-type
          rnd (Random.)
          u (Unit. ut 0 0 1 1 1 rnd nil)]
      (is (= (UnitCommand. UnitState/ATTACK 1) (.-currentCommand u))))))
