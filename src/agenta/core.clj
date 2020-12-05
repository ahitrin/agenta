(ns agenta.core
  (:require agenta.ui)
  (:import (agenta Engine PanelViewer SingleRandom UnitTypeImpl GameMap)))

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

(defn -main [& args]
  (let [p (new PanelViewer)
        u (clojure.edn/read-string (slurp "placement.edn"))
        r (SingleRandom/get)
        m (GameMap. r)
        e (Engine. m)
        f (agenta.ui/wrap-into-frame "Agenta demo" p)]
    (.init e r  (:player0 u) (:player1 u) default-units)
    (.addViewer e p)
    (while (== (.getWinner e) -1)
        (.step e))
    (agenta.ui/show-end-message f p (format "Player %d has won after %d ticks!"
                                            (.getWinner e)
                                            (.getTicks e)))))
