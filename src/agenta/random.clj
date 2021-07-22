(ns agenta.random
  (:import (agenta SingleRandom)))

(defn rnd-xy!
  "Makes a random pair of coordinates within given boundaries"
  [^SingleRandom r mx my]
  (vec [(.nextInt r mx) (.nextInt r my)]))
