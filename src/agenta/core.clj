(ns agenta.core
  (:require agenta.ui)
  (:import (agenta Engine PanelViewer SingleRandom UnitTypeImpl GameMap Unit)))

(def default-units
  "This piece of data is a good candidate for extraction into edn file"
  [(doto
     (UnitTypeImpl.)
     (.setName "Footman")
     (.setBaseAttack 5)
     (.setRandAttack 6)
     (.setRange 1.45)
     (.setAttackSpeed 25)
     (.setVisibility 7)
     (.setSpeed 40)
     (.setHitPoints 70))
   (doto
     (UnitTypeImpl.)
     (.setName "Archer")
     (.setBaseAttack 3)
     (.setRandAttack 5)
     (.setRange 5.5)
     (.setAttackSpeed 40)
     (.setVisibility 7)
     (.setSpeed 40)
     (.setHitPoints 55))
   (doto
     (UnitTypeImpl.)
     (.setName "Knight")
     (.setBaseAttack 9)
     (.setRandAttack 7)
     (.setRange 1.45)
     (.setAttackSpeed 40)
     (.setVisibility 7)
     (.setSpeed 25)
     (.setHitPoints 110))])

(defn init-units [^SingleRandom r ^GameMap m player0-units player1-units unit-types]
  (let [player-units {0 player0-units 1 player1-units}
        new-units (for [ut unit-types
                        p (range 2)
                        c (range 100)
                        :let [max-num (get (get player-units p)
                                           (.toLowerCase (.getName ut)) 0)]
                        :when (< c max-num)]
                    (Unit. ut p m r))]
    (doseq [unit new-units]
      (.placeWherePossible m unit))
    new-units))

(defn -main [& args]
  (let [p (new PanelViewer)
        u (clojure.edn/read-string (slurp "placement.edn"))
        r (SingleRandom/get)
        m (GameMap. r)
        u (init-units r m (:player0 u) (:player1 u) default-units)
        e (Engine. m u)
        f (agenta.ui/wrap-into-frame "Agenta demo" p)]
    (.renderTrees m)
    (.addViewer e p)
    (while (== (.getWinner e) -1)
        (.step e))
    (agenta.ui/show-end-message f p (format "Player %d has won after %d ticks!"
                                            (.getWinner e)
                                            (.getTicks e)))))
