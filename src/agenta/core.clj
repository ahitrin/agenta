(ns agenta.core
  (:require [agenta.engine :as eng]
            [agenta.ui :as ui])
  (:import (javax.swing JFrame)))

(defn -main [& args]
  (let [s (clojure.edn/read-string (slurp "setting/demo.edn"))
        f (JFrame. "Agenta demo")
        p (ui/make-viewer f)
        v (ui/wrap-viewer p)
        result (eng/run s v)]
      (ui/show-end-message f p (format "Player %d has won after %d ticks!"
                                       (:winner result)
                                       (:steps result)))))
