(ns agenta.core
  (:import (agenta CommandLineInitiator DefaultUnits Engine PanelViewer PanelViewerFrame SingleRandom)))

(defn -main [& args]
  (let [p (new PanelViewer)
        cli (new CommandLineInitiator "placement.txt")]
    (.load cli)
    (let [e (new Engine (.getParameters cli) (SingleRandom/get))
          f (new PanelViewerFrame "Agenta example" p)]
      (.init e (DefaultUnits/build))
      (.addViewer e p)
      (while (== (.getWinner e) -1)
          (.step e))
      (.showEndMessage f p (String/format "Player %d has won after %d ticks!"
                                          (.getWinner e)
                                          (.getTicks e))))))
