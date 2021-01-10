(ns agenta.core
  (:require [agenta.engine :as eng]
            [agenta.ui :as ui])
  (:import (agenta PanelViewer)
           (javax.swing JFrame)
           (java.util.function Consumer)))

(defn -main [& args]
  (let [s (clojure.edn/read-string (slurp "setting/demo.edn"))
        f (JFrame. "Agenta demo")
        p (PanelViewer. (proxy [Consumer] [] (accept [p1] (ui/wrap-into-frame f p1))))
        result (eng/run s (list p))]
      (ui/show-end-message f p (format "Player %d has won after %d ticks!"
                                       (:winner result)
                                       (:steps result)))))
