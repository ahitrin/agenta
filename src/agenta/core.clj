(ns agenta.core
  (:require [agenta.engine :as eng]
            [agenta.random :as rnd]
            [agenta.ui :as ui])
  (:import (javax.swing JFrame)
           (agenta ImagePanel)))

(defn -main [& args]
  (rnd/init!)
  (let [i (if (seq? args) (first args) "demo")
        s (clojure.edn/read-string (slurp (format "setting/%s.edn" i)))
        f (JFrame. "Agenta demo")
        p (ImagePanel.)
        v (ui/wrap-viewer f p)
        result (eng/run-game! s v)]
      (ui/show-end-message f p (format "Player %d has won after %d ticks!"
                                       (:winner result)
                                       (:steps result)))))
