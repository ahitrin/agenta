(ns agenta.core
  (:require [agenta.engine :as eng]
            [agenta.fxui :as ui]
            [agenta.random :as rnd]))

(defn -main [& args]
  (rnd/init!)
  (let [i (if (seq? args) (first args) "demo")
        s (clojure.edn/read-string (slurp (format "setting/%s.edn" i)))
        v (ui/update-state!)]
    (ui/show)
    (eng/run-game! s v)))
