(require '[nextjournal.clerk :as clerk]
         '[agenta.counter :as ctr])
;; # Standartization of internal API

;; Status: in progress

;; ## Rationale
;; API of internal functions was developed stochastically over time.
;; Some functions were simply translated from Java code, some others were created to satisfy short-term needs.
;; There's a little standartization over them currently.
;; We use similar (yet not same) patterns over the code.
;; It increases cognitive load because a signature of every single function have to be remembered.

;; Here I want to overview signatures of existing functions, discover common patterns, and change signatures in order to match these patterns.
;; As it was said, "it's better to have 1 data structure with 100 functions over it than 10 data structures with 10 functions each" (or something like that).

;; ## Current API

;; ### unit.clj
;; Depends on `perk.clj`.

;; An unnamed (no corresponding `defrecord`) map of unit/actor properties:

{; "static" properties (do not change during game)
 :max-spd        10
 :max-health     25
 :visibility     7
 :base-attack    1
 :rnd-attack     6
 :range          1.7
 :select-perk    'agenta.perk/select-random-memoized
 :move-perk      'agenta.perk/move-to-friends
 :img            "footman"
 :id             12
 :player         0
 :name           "Pooh Warrior0"
 ; "dynamic" properties (change during game)
 :attack-counter (ctr/make 3 5)
 :health-counter (ctr/make 50 100)
 :speed-counter  (ctr/make 7 10)
 :think-counter  (ctr/make 2 3)
 :health         25
 :state          :attack
 :kills          0}

;; Functions:

^{::clerk/visibility {:code :hide}}
(clerk/table {
  :head ["name" "arguments" "output"]
  :rows [
    ["make-unit"    ['unit-def]         'unit]
    ["update-state" ['unit 'int 'int]   'unit]
    ["friends?"     ['unit 'unit]       'boolean]]})

;; ### perk.clj

;; Does not explicitly depend on `unit` or `engine`.
;; Nevertheless, it relies on some data from `unit` implcitly.

;; There are two classes of functions here (look like candidates for `defprotocol`).

^{::clerk/visibility {:code :hide}}
(clerk/table {
  :head ["name" "arguments" "output"]
  :rows [
    ["select-XX"    ['unit '[unit]]     {:target 'unit}]
    ["move-XX"      ['xy 'unit '[unit]] {:type :move :dx 'int :dy 'int}]]})

;; ### game_map.clj

;; Do not depend on any structures from above, but keep them within `GameMap` record.

^{::clerk/visibility {:code :hide}}
'(defrecord GameMap [size-x size-y cells objs id-to-xy])

^{::clerk/visibility {:code :hide}}
(clerk/table {
  :head ["name" "type"]
  :rows [
    ['size-x    'int]
    ['size-y    'int]
    ['cells     "{xy -> :grass/:tree}"]
    ['objs      "{xy -> unit}"]
    ['id-to-xy  "{int -> xy}"]]})

;; Also, it uses so-called `map-spec` structure that's read from setting file.

{:size-x    16
 :size-y    16
 :type      'agenta.game-map/forest}

;; Functions, a lot of them:

^{::clerk/visibility {:code :hide}}
(clerk/table {
  :head ["name" "arguments" "output"]
  :rows [
    ["new-map"          [`GameMap "fn({xy->unit} -> {xy->unit})"]   'GameMap]
    ["plane"            ['int 'int]                                 {}]
    ["forest"           ['int 'int]                                 "{xy -> :tree}"]
    ["xy-to-unit"       ['GameMap]                                  "{xy -> unit}"]
    ["object-at"        ['GameMap 'xy]                              'unit]
    ["cell-type"        ['GameMap 'xy]                              ":grass/:tree"]
    ["can-place?"       ['GameMap 'xy]                              'boolean]
    ["make-map"         ['map-spec '[unit]]                         'GameMap]
    ["obj-by-id"        ['GameMap 'int]                             'unit]
    ["objects-in-raius" ['GameMap 'int 'double]                     '[unit]]
    ]})

;; ### engine.clj

;; Uses all data types and structures from above.
;; Also, the following structure from setting file is used.

{
 :description "Scenario description"
 :map         {
               :size-x 18
               :size-y 18
               :type   "agenta.game-map/plane"
               }
 :unit-types  [ ;; A list of "unit-def"s
               {:name        "Warrior"
                :attackSpeed 3
                :baseAttack  1
                :randAttack  4
                :hitPoints   10
                :speed       3
                :thinkSpeed  3
                :visibility  10
                :range       1.45
                :image       "footman"
                :perk        {
                              :select "agenta.perk/select-random-memoized"
                              :move   "agenta.perk/move-randomly"
                              }}
               ]
 :placement   {
               0 {"warrior" 10}
               1 {"warrior" 10}
               }
 :experiment  {
               :max-ticks 500
               :total     100
               }
 }

;; This structure would be mentioned below as a "setting".

;; Another candidate for `defrecord`: so called "action".

{:target 'int :dx 'int :dy 'int :type ":attack/:move"}

;; "Viewer" is a function responsible for displaying current state.
;; Probably, it should be extracted into a protocol?
;; Arguments: `GameMap, {:winner int :tick int}`.

;; "Stopper" is a function responsible for breaking execution loop.
;; It gains no arguments, and returns `boolean` value.

;; Functions:

^{::clerk/visibility {:code :hide}}
(clerk/table {
  :head ["name" "arguments" "output"]
  :rows [
    ["into-defs"        ['setting]                                  ['unit-def]]
    ["init-units"       ['setting]                                  ['unit]]
    ["choose-enemy"     ['unit ["{xy -> unit}"]]                    'unit]
    ["act!"             ['xy 'unit ["{xy -> unit}"]]                'action]
    ["unit-action!"     [['xy 'unit] 'GameMap]                      ['unit 'action 'xy]]
    ["perform-attack!"  ['GameMap 'unit 'action 'xy]                'GameMap]
    ["perform-move"     ['GameMap 'unit 'action 'xy]                'GameMap]
    ["apply-action!"    ['GameMap ['unit 'action 'xy]]              'GameMap]
    ["apply-actions!"   [['action] 'GameMap]                        'GameMap]
    ["on-hp-tick"       ['GameMap]                                  'GameMap]
    ["tick-health"      ["{xy -> unit}"]                            "{xy -> unit}"]
    ["init-game"        ['setting]                                  {:map 'GameMap :tick 'int :end-tick 'int}]
    ["run-loop!"        ['viewer 'GameMap 'int 'int 'int 'viewer]   {:winner 'int :steps 'int}]
    ["run-game!"        ['setting 'viewer]                          {:winner 'int :steps 'int}]
    ]})

;; ### fxui.clj

;; It uses a reference to `engine/run-loop` function.
;; This reference is displayed as `'run-fn`.

;; An internal game state is an `atom` containing the following fields:

{:stage ":init/:run/:pause"
 :winner 'int
 :tick 'int
 :end-tick 'int
 :game-map 'GameMap
 :run-fn 'run-fn}

;; I do not show all functions here because most of them should be considered private.
;; Instead, only functions used in sibling namespaces (`core` and `engine`) would be mentioned.

^{::clerk/visibility {:code :hide}}
(clerk/table {
  :head ["name" "arguments" "output"]
  :rows [
    ["pause-game"       "[] (*state)" 'boolean]
    ["update-state!"    ['GameMap {:keys [:tick :winner]}] "- (*state)"]
    ["show"             ['GameMap 'run-fn] "-"]
    ]})

;; `pause-game` actually implements "stopper protocol", and `update-state!` implements "viewer protocol".

;; ## Observations and Hypotheses

;; 1. Functions `engine/into-defs` should be moved into `unit` ns.
;; Probably, `init-units` too.
;; (temp: applied)

;; 2. There's a duplication between `unit` and `unit-def` structures.
;; It should be removed.

;; 3. There're a lot of usages of mapping `{xy -> unit}`.
;; Probably, we should introduce a name for it?

;; 4. Overall game state should contain the following fields:
;; `{:map 'GameMap :tick 'int :end-tick 'int :winner 'int}`.
;; A field `:steps` in `viewer` function should be replaced with `:tick`.

;; 5. We should also synchronize an internal state in `fxui` with this record.
;; Should we move `:stage` and `:run-fn` in it?
;; Or it's better to keep them only on UI side?

;; 6. We should rename `pause-game` into `pause-game!` because it's not pure.

;; 

;; TODO

;; ## Expected results

;; What would we receive after an implementation of proposals from above.

;; ### unit.clj (new)
;; Depends on `perk.clj`.

;; An unnamed (no corresponding `defrecord`) map of unit/actor properties:

{; "static" properties (do not change during game)
 :max-spd        10
 :max-health     25
 :visibility     7
 :base-attack    1
 :rnd-attack     6
 :range          1.7
 :select-perk    'agenta.perk/select-random-memoized
 :move-perk      'agenta.perk/move-to-friends
 :img            "footman"
 :id             12
 :player         0
 :name           "Pooh Warrior0"
 ; "dynamic" properties (change during game)
 :attack-counter (ctr/make 3 5)
 :health-counter (ctr/make 50 100)
 :speed-counter  (ctr/make 7 10)
 :think-counter  (ctr/make 2 3)
 :health         25
 :state          :attack
 :kills          0}

;; Functions:

^{::clerk/visibility {:code :hide}}
(clerk/table {
  :head ["name" "arguments" "output"]
  :rows [
    ["into-defs"    ['setting]          ['unit-def]]
    ["init-units"   ['setting]          ['unit]]
    ["make-unit"    ['unit-def]         'unit]
    ["update-state" ['unit 'int 'int]   'unit]
    ["friends?"     ['unit 'unit]       'boolean]
    ]})

;; ### perk.clj (new)

;; Does not explicitly depend on `unit` or `engine`.
;; Nevertheless, it relies on some data from `unit` implcitly.

;; There are two classes of functions here (look like candidates for `defprotocol`).

^{::clerk/visibility {:code :hide}}
(clerk/table {
  :head ["name" "arguments" "output"]
  :rows [
    ["select-XX"    ['unit '[unit]]     {:target 'unit}]
    ["move-XX"      ['xy 'unit '[unit]] {:type :move :dx 'int :dy 'int}]]})

;; ### game_map.clj (new)

;; Do not depend on any structures from above, but keep them within `GameMap` record.

^{::clerk/visibility {:code :hide}}
'(defrecord GameMap [size-x size-y cells objs id-to-xy])

^{::clerk/visibility {:code :hide}}
(clerk/table {
  :head ["name" "type"]
  :rows [
    ['size-x    'int]
    ['size-y    'int]
    ['cells     "{xy -> :grass/:tree}"]
    ['objs      "{xy -> unit}"]
    ['id-to-xy  "{int -> xy}"]]})

;; Also, it uses so-called `map-spec` structure that's read from setting file.

{:size-x    16
 :size-y    16
 :type      'agenta.game-map/forest}

;; Functions, a lot of them:

^{::clerk/visibility {:code :hide}}
(clerk/table {
  :head ["name" "arguments" "output"]
  :rows [
    ["new-map"          [`GameMap "fn({xy->unit} -> {xy->unit})"]   'GameMap]
    ["plane"            ['int 'int]                                 {}]
    ["forest"           ['int 'int]                                 "{xy -> :tree}"]
    ["xy-to-unit"       ['GameMap]                                  "{xy -> unit}"]
    ["object-at"        ['GameMap 'xy]                              'unit]
    ["cell-type"        ['GameMap 'xy]                              ":grass/:tree"]
    ["can-place?"       ['GameMap 'xy]                              'boolean]
    ["make-map"         ['map-spec '[unit]]                         'GameMap]
    ["obj-by-id"        ['GameMap 'int]                             'unit]
    ["objects-in-raius" ['GameMap 'int 'double]                     '[unit]]
    ]})

;; ### engine.clj (new)

;; Uses all data types and structures from above.
;; Also, the following structure from setting file is used.

{
 :description "Scenario description"
 :map         {
               :size-x 18
               :size-y 18
               :type   "agenta.game-map/plane"
               }
 :unit-types  [ ;; A list of "unit-def"s
               {:name        "Warrior"
                :attackSpeed 3
                :baseAttack  1
                :randAttack  4
                :hitPoints   10
                :speed       3
                :thinkSpeed  3
                :visibility  10
                :range       1.45
                :image       "footman"
                :perk        {
                              :select "agenta.perk/select-random-memoized"
                              :move   "agenta.perk/move-randomly"
                              }}
               ]
 :placement   {
               0 {"warrior" 10}
               1 {"warrior" 10}
               }
 :experiment  {
               :max-ticks 500
               :total     100
               }
 }

;; This structure would be mentioned below as a "setting".

;; Another candidate for `defrecord`: so called "action".

{:target 'int :dx 'int :dy 'int :type ":attack/:move"}

;; "Viewer" is a function responsible for displaying current state.
;; Probably, it should be extracted into a protocol?
;; Arguments: `GameMap, {:winner int :tick int}`.

;; "Stopper" is a function responsible for breaking execution loop.
;; It gains no arguments, and returns `boolean` value.

;; Functions:

^{::clerk/visibility {:code :hide}}
(clerk/table {
  :head ["name" "arguments" "output"]
  :rows [
    ["choose-enemy"     ['unit ["{xy -> unit}"]]                    'unit]
    ["act!"             ['xy 'unit ["{xy -> unit}"]]                'action]
    ["unit-action!"     [['xy 'unit] 'GameMap]                      ['unit 'action 'xy]]
    ["perform-attack!"  ['GameMap 'unit 'action 'xy]                'GameMap]
    ["perform-move"     ['GameMap 'unit 'action 'xy]                'GameMap]
    ["apply-action!"    ['GameMap ['unit 'action 'xy]]              'GameMap]
    ["apply-actions!"   [['action] 'GameMap]                        'GameMap]
    ["on-hp-tick"       ['GameMap]                                  'GameMap]
    ["tick-health"      ["{xy -> unit}"]                            "{xy -> unit}"]
    ["init-game"        ['setting]                                  {:map 'GameMap :tick 'int :end-tick 'int}]
    ["run-loop!"        ['viewer 'GameMap 'int 'int 'int 'viewer]   {:winner 'int :steps 'int}]
    ["run-game!"        ['setting 'viewer]                          {:winner 'int :steps 'int}]
    ]})

;; ### fxui.clj (new)

;; It uses a reference to `engine/run-loop` function.
;; This reference is displayed as `'run-fn`.

;; An internal game state is an `atom` containing the following fields:

{:stage ":init/:run/:pause"
 :winner 'int
 :tick 'int
 :end-tick 'int
 :game-map 'GameMap
 :run-fn 'run-fn}

;; I do not show all functions here because most of them should be considered private.
;; Instead, only functions used in sibling namespaces (`core` and `engine`) would be mentioned.

^{::clerk/visibility {:code :hide}}
(clerk/table {
  :head ["name" "arguments" "output"]
  :rows [
    ["pause-game"       "[] (*state)" 'boolean]
    ["update-state!"    ['GameMap {:keys [:tick :winner]}] "- (*state)"]
    ["show"             ['GameMap 'run-fn] "-"]
    ]})

;; `pause-game` actually implements "stopper protocol", and `update-state!` implements "viewer protocol".

;; TODO
