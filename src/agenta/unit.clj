(ns agenta.unit
  (:require [agenta.counter :as ctr]
            [agenta.perk :as pk]            ; we need to import perk for (resolve)
            [agenta.random :as rnd])
  (:import (com.github.javafaker Faker)))

(defn make-unit [unit-type]
  "Create one unit dictionary from given specs"
  {
   ; "static" properties (do not change during game)
   :max-spd        (:speed unit-type)
   :max-health     (:hitPoints unit-type)
   :visibility     (:visibility unit-type)
   :base-attack    (:baseAttack unit-type)
   :rnd-attack     (:randAttack unit-type)
   :range          (:range unit-type)
   :select-perk    (resolve (.get (:perk unit-type) :select))
   :move-perk      (resolve (.get (:perk unit-type) :move))
   :img            (str (:image unit-type) (:player unit-type))
   :id             (:id unit-type)
   :player         (:player unit-type)
   :name           (format "%s %s%d"
                           (.firstName (.name (Faker.)))
                           (:name unit-type)
                           (:player unit-type))
   ; "dynamic" properties (change during game)
   :attack-counter (ctr/make (inc (rnd/i! (:attackSpeed unit-type))) (:attackSpeed unit-type))
   :health-counter (ctr/make (inc (rnd/i! 100)) 100)
   :speed-counter  (ctr/make (inc (rnd/i! (:speed unit-type))) (:speed unit-type))
   :think-counter  (ctr/make (inc (rnd/i! 3)) 3)
   :health         (:hitPoints unit-type)
   :state          :attack
   :kills          0})
