(ns agenta.core
  (:require [agenta.engine :as eng]
            [agenta.game-map :as gm]
            [agenta.perk]
            [agenta.ui :as ui])
  (:import (agenta Engine PanelViewer SingleRandom Unit)))

(defn init-units [^SingleRandom r setting]
  (for [ut (:unit-types setting)
        p (range 2)
        c (range 100)
        :let [max-num (get (get (:placement setting) p)
                           (.toLowerCase (:name ut)) 0)]
        :when (< c max-num)]
    (Unit. (eng/make-unit ut) p r ((resolve (.get (:perk ut) "select")) r))))

(defn -main [& args]
  (let [s (clojure.edn/read-string (slurp "setting/demo.edn"))
        r (SingleRandom/get)
        m (gm/make-map r (:map s))
        p (PanelViewer. m)
        u (init-units r s)
        e (Engine. m u (list p))
        f (ui/wrap-into-frame "Agenta demo" p)]
    (doseq [unit u] (.placeWherePossible m unit))
    (while (== (.getWinner e) -1)
      (.step e))
    (ui/show-end-message f p (format "Player %d has won after %d ticks!"
                                     (.getWinner e)
                                     (.getTicks e)))))
