(ns agenta.core
  (:require [agenta.engine :as eng]
            [agenta.game-map :as gm]
            [agenta.ui :as ui])
  (:import (agenta Engine PanelViewer SingleRandom)
           (javax.swing JFrame)
           (java.util.function Consumer)))

(defn -main [& args]
  (let [s (clojure.edn/read-string (slurp "setting/demo.edn"))
        r (SingleRandom/get)
        m (gm/make-map r (:map s))
        f (JFrame. "Agenta demo")
        p (PanelViewer. (proxy [Consumer] [] (accept [p1] (ui/wrap-into-frame f p1))))
        u (eng/init-units r s)
        e (Engine. m u (list p))]
    (doseq [unit u] (.placeWherePossible m unit))
    (while (== (.getWinner e) -1)
      (.step e))
    (ui/show-end-message f p (format "Player %d has won after %d ticks!"
                                     (.getWinner e)
                                     (.getTicks e)))))
