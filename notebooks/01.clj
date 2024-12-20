(require '[nextjournal.clerk :as clerk])
;; # Agent's model of the world

;; Status: in progress

;; ## Rationale
;; One of initial goals of this project is to develop a "human-like" approach to AI.
;; Every agent (the same as "unit", currently) should behave only on some _local_ information it has.
;; This information is also named as "model of the world".

;; ## Unit's local model in Agenta

;; Let's create some environment first.

(agenta.random/init! 1234567)

(def setting (clojure.edn/read-string (slurp "setting/demo.edn")))
(def start-map (agenta.game-map/make-map setting))
(def start-units (vals (:objs start-map)))

;; Every unit has some information about self, and some information about its neighbourhood.

(def visible-objects
  (let [u (first start-units)]
    (agenta.game-map/objects-in-radius start-map (:id u) (:visibility u))))

;; The tricky question is how to interpret this data in a generic way suitable for a wide variety of "perks".
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

(clerk/plotly {:data [{:x [4 -2 6 3 -2 5] :y [-4 -4 -1 2 1 -4]
                       :mode "markers+text" :type "scatter"
                       :name "Friends" :textposition "top center"
                       :text ["#1 (70)" "#7 (70)" "#24 (55)" "#25 (55)" "#29 (55)" "#40 (110)"]}
                      {:x [-1 5 -2 5] :y [-3 -3 -1 1]
                       :mode "markers+text" :type "scatter"
                       :name "Enemies" :textposition "top center"
                       :text ["#11 (70)" "#15 (70)" "#31 (55)" "#37 (55)"]}
                      {:x [0] :y [0] :mode "markers" :type "scatter" :name "Self"}]
               :config {:displayModeBar false
                        :displayLogo false}})

;; _The same table as above, but in 2d form._

;; ## Road to generalized perks form

;; Currently, all unit perks are separated into various types, and each type one has its own interface.
;; Just compare:

(comment
(defn select-random
  "Given several units select one of them absolutely randomly"
  [actor units]
  {:target (.get units (rnd/i! (count units)))}))

;; This is a `:select-perk`.
;; It receieves a current actor and a list of _pre-filtered_ enemies.
;; It returns `:target` value which is then transformed into action by engine.

(comment
(defn move-to-friends [xy actor visible-objects]
  "Move towards the center of friendly units of any type"
  (let [player (:player actor)
        friends (filter #(= player (:player (second %))) visible-objects)
        dx (->> friends (map first) (map :x) (map #(- % (:x xy))) (reduce +) float Math/signum int)
        dy (->> friends (map first) (map :y) (map #(- % (:y xy))) (reduce +) float Math/signum int)]
    {:type :move :dx dx :dy dy})))

;; This is a `:move-perk`.
;; It receives current coordinates, a current user, and **all** visible objects (both friends and enemies).
;; It perform selection of friends and calculates vectors to them by itself.
;; It returns already "baked" action.

;; Next steps here are the following:
;; 1. We need to use common format in these perk types, both for input and for output.
;; 2. We need to convert existing out-of-perk pieces of AI into new perks.
;; 3. A procedure to select decision from all perks must be introduced.
