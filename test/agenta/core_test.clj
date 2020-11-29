(ns agenta.core-test
  (:require [clojure.test :refer :all]
            [agenta.core :refer :all])
  (:import (agenta GameMap SingleRandom Unit UnitTypeImpl)))


(deftest unit-is-alive-by-default
         (testing "Unit is alive by default"
                  (let [type (doto
                               (UnitTypeImpl.)
                               (.setSpeed 3)
                               (.setAttackSpeed 3)
                               (.setHitPoints 10))
                        random (SingleRandom/get)
                        map (new GameMap random)
                        unit (new Unit type 13 map random)]
                       (is (.isAlive unit)))))
