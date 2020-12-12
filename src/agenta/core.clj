(ns agenta.core
  (:require [agenta.game-map :as gm]
            [agenta.perk :as perk]
            [agenta.ui :as ui])
  (:import (agenta Engine PanelViewer SingleRandom UnitTypeImpl Unit)))

(defn make-unit [spec]
  (doto (UnitTypeImpl.)
    (.setName (:name spec))
    (.setBaseAttack (:baseAttack spec))
    (.setRandAttack (:randAttack spec))
    (.setRange (:range spec))
    (.setAttackSpeed (:attackSpeed spec))
    (.setVisibility (:visibility spec))
    (.setSpeed (:speed spec))
    (.setHitPoints (:hitPoints spec))
    (.setImage (:image spec))))

(defn init-units [^SingleRandom r setting]
  (for [ut (:unit-types setting)
        p (range 2)
        c (range 100)
        :let [max-num (get (get (:placement setting) p)
                           (.toLowerCase (:name ut)) 0)]
        :when (< c max-num)]
    (Unit. (make-unit ut) p r ((perk/perks (.get (:perk ut) "select")) r))))

(defn -main [& args]
  (let [s (clojure.edn/read-string (slurp "setting/demo.edn"))
        p (new PanelViewer)
        r (SingleRandom/get)
        m (gm/make-map r (:map s))
        u (init-units r s)
        e (Engine. m u)
        f (ui/wrap-into-frame "Agenta demo" p)]
    (doseq [unit u] (.placeWherePossible m unit))
    (.addViewer e p)
    (while (== (.getWinner e) -1)
      (.step e))
    (ui/show-end-message f p (format "Player %d has won after %d ticks!"
                                     (.getWinner e)
                                     (.getTicks e)))))
