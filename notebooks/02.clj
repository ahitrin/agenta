^{::clerk/visibility {:code :hide}}
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

;; TODO

;; ## Observations and Hypotheses

;; TODO
