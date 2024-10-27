(ns agenta.unit
  (:require [agenta.counter :as ctr]
            [agenta.perk :as pk]            ; we need to import perk for (resolve)
            [agenta.random :as rnd])
  (:import (com.github.javafaker Faker)))

(defn make-unit [unit-type]
  "Create one unit dictionary from given specs"
  {; "static" properties (do not change during game)
   :max-spd        (:max-spd unit-type)
   :max-health     (:hitPoints unit-type)
   :visibility     (:visibility unit-type)
   :base-attack    (:baseAttack unit-type)
   :rnd-attack     (:randAttack unit-type)
   :range          (:range unit-type)
   :select-perk    (resolve (.get (:perk unit-type) :select))
   :move-perk      (resolve (.get (:perk unit-type) :move))
   :img            (str (:image unit-type) (:player unit-type))
   :player         (:player unit-type)
   :type           (.toLowerCase (:name unit-type))
   :name           (format "%s %s%d"
                           (.firstName (.name (Faker.)))
                           (:name unit-type)
                           (:player unit-type))
   ; "dynamic" properties (change during game)
   :attack-counter (ctr/make (inc (rnd/i! (:attackSpeed unit-type))) (:attackSpeed unit-type))
   :health-counter (ctr/make (inc (rnd/i! 100)) 100)
   :speed-counter  (ctr/make (inc (rnd/i! (:max-spd unit-type))) (:max-spd unit-type))
   :think-counter  (ctr/make (inc (rnd/i! (:thinkSpeed unit-type))) (:thinkSpeed unit-type))
   :health         (:hitPoints unit-type)
   :state          :attack
   :kills          0})

(defn into-defs [setting]
  "Transform setting object into a list of unit defs (specs)"
  (for [ut  (:unit-types setting)
        p   (range 2)
        _   (range (-> (:placement setting)
                       (get p)
                       (get (.toLowerCase (:name ut)))
                       :count))]
    (assoc ut :player p)))

(defn init-units [setting]
  "Create all units described by setting"
  (let [defs    (into-defs setting)
        units   (map make-unit defs)
        units+  (map-indexed #(assoc %2 :id %) units)]
    units+))

(defn pretty [unit]
  (-> unit
      (assoc :hp (format "%d/%d" (:health unit) (:max-health unit)))
      (select-keys [:kills :name :state :id :hp])))

(defn update-state [actor hp max-hp]
  (let [escape-threshold (int (/ max-hp 5))
        attack-threshold (int (/ max-hp 4))
        old-state (:state actor)
        new-state (cond
                    (< hp escape-threshold) :escape
                    (>= hp attack-threshold) :attack
                    :else old-state)]
    (assoc actor :state new-state)))

(defn friends? [u u']
  (= (:player u) (:player u')))
