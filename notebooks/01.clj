(require '[nextjournal.clerk :as clerk])
;; # Agent's model of the world

;; ## Rationale
;; One of initial goals of this project is to develop a "human-like" approach to AI.
;; Every agent (the same as "unit", currently) should behave only on some _local_ information it has.
;; This information is also named as "model of the world".

;; ## Unit's local model in Agenta

;; Let's create some environment first.

(agenta.random/init! 1234567)

(def setting (clojure.edn/read-string (slurp "setting/demo.edn")))
(def start-units (agenta.engine/init-units setting))
(def start-map (agenta.game-map/make-map (:map setting) start-units))

;; Every unit has some information about self, and some information about its neighbourhood.

(def visible-objects
  (let [u (first start-units)]
    (agenta.game-map/objects-in-radius start-map (:id u) (:visibility u))))

;; The tricky question is how to interpret this data in a generic way suitable for a wide variaty of "perks".
;; In one hand, we want to provide enough amount information to make it possible to generate various data processing logic.
;; In other hand, it shouldn't be too complex.

(let [u (first start-units)
      xy (first (agenta.game-map/obj-by-id start-map (:id u)))
      vo visible-objects
      data (map #(vector (:id (second %))
                (agenta.unit/friends? u (second %))
                (agenta.math/v-> xy (first %))
                (:health (second %))) vo)]
  (clerk/table
  (clerk/use-headers (cons ["Unit Id" "Friend?" "Distance" "health"] (sort-by first data)))))

;; That's, probably, is a maximum information that we want to be available for a single unit.
;; * Unit id: definitely yes, it's needed for targeting;
;; * Friend or not: definitely yes, it's needed for targeting and moving;
;; * Distance: definitely yes, it's needed for moving;
;; * Health: probably yes;
;; * Other fields: probably later but not now:
;;   * Unit type;
;;   * Maybe, some others;

;; Having this information, a unit must decide what should it do:
;; * Attack some enemy unit? Which one?
;; * Move? In which direction?

;; There could also be other sources of actions, like internal state of unit (is it too wounded? if yes, it could decline to attack) or commands from commander (to be re-implemented yet).
;; We could ignore these sources for now.
;; A simple table above is the only important thing for now.
