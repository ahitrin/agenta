(ns agenta.core
  (:require [agenta.engine :as eng]
            [agenta.fxui :as ui]
            [agenta.random :as rnd]))

(defn -main [& args]
  (rnd/init!)
  (let [i (if (seq? args) (first args) "demo")
        s (clojure.edn/read-string (slurp (format "setting/%s.edn" i)))
        g (eng/init-game s)]
    (ui/show g)
    (eng/run-loop! ui/update-state! (:map g) -1 (:tick g) (:end-tick g) (fn [] false))))
