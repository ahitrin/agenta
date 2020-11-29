(ns agenta.core
  (:import (agenta DefaultUnits Engine PanelViewer PanelViewerFrame SingleRandom)))

(defn -main [& args]
  (let [p (new PanelViewer)
        u (clojure.edn/read-string (slurp "placement.edn"))
        e (new Engine (SingleRandom/get) (:player0 u) (:player1 u))
        f (new PanelViewerFrame "Agenta example" p)]
    (.init e (DefaultUnits/build))
    (.addViewer e p)
    (while (== (.getWinner e) -1)
        (.step e))
    (.showEndMessage f p (String/format "Player %d has won after %d ticks!"
                                        (object-array [(.getWinner e)
                                                       (.getTicks e)])))))
