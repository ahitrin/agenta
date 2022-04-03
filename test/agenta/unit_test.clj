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
        (getHealthLimit [lvl] (- 5 lvl))
        (toString [] "Peasant")))

(defn- attack [prio] (UnitCommand. UnitState/ATTACK prio))

(defn- stand [prio] (UnitCommand. UnitState/STAND prio))

(defn- escape [prio] (UnitCommand. UnitState/ESCAPE prio))

(defn- make-unit []
  (Unit. fake-unit-type 0 0 1 1 1 (Random.) nil))

(deftest default-state
  (testing "unit default state"
    (let [u (make-unit)]
      (is (= (attack 1) (.-currentCommand u))))))

(deftest no-change-in-think
  (testing "state remains the same when nothing happens"
    (let [u (make-unit)]
      (.doThink u)
      (is (= (attack 1) (.-currentCommand u))))))

(deftest state-change-on-higher-prio
  (testing "state could be changed by sending an order with higher priority"
    (let [u (make-unit)]
      (.obtain u (attack 2))
      (.doThink u)
      (is (= (attack 2) (.-currentCommand u))))))

(deftest state-no-change-on-lower-prio
  (testing "state could not be changed by sending an order with lower priority"
    (let [u (make-unit)]
      (.obtain u (attack 3))
      (.obtain u (escape 2))
      (.doThink u)
      (is (= (attack 3) (.-currentCommand u))))))
