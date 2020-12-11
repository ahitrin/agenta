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
     (.setHitPoints 70)
     (.setImage "footman"))
   (doto
     (UnitTypeImpl.)
     (.setName "Archer")
     (.setBaseAttack 3)
     (.setRandAttack 5)
     (.setRange 5.5)
     (.setAttackSpeed 40)
     (.setVisibility 7)
     (.setSpeed 40)
     (.setHitPoints 55)
     (.setImage "archer"))
   (doto
     (UnitTypeImpl.)
     (.setName "Knight")
     (.setBaseAttack 9)
     (.setRandAttack 7)
     (.setRange 1.45)
     (.setAttackSpeed 40)
     (.setVisibility 7)
     (.setSpeed 25)
     (.setHitPoints 110)
     (.setImage "knight"))])

(defn init-units [^SingleRandom r setting unit-types]
  (for [ut unit-types
        p (range 2)
        c (range 100)
        :let [max-num (get (get (:placement setting) p)
                           (.toLowerCase (.getName ut)) 0)]
        :when (< c max-num)]
    (Unit. ut p r)))

(defn -main [& args]
  (let [s (clojure.edn/read-string (slurp "setting/demo.edn"))
        p (new PanelViewer)
        r (SingleRandom/get)
        m (GameMap. r)
        u (init-units r s default-units)
        e (Engine. m u)
        f (agenta.ui/wrap-into-frame "Agenta demo" p)]
    (.renderTrees m)
    (doseq [unit u] (.placeWherePossible m unit))
    (.addViewer e p)
    (while (== (.getWinner e) -1)
        (.step e))
    (agenta.ui/show-end-message f p (format "Player %d has won after %d ticks!"
                                            (.getWinner e)
                                            (.getTicks e)))))
