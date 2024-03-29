(ns agenta.experiment
  (:require [agenta.engine :as eng]
            [agenta.random :as rnd]
            [clojure.tools.logging :as log]))

(defn single-run [setting]
  (rnd/init!)
  (let [result (eng/run-game! setting (fn [m _] ()))]
    (log/infof (format "Player %d has won after %d ticks" (:winner result) (:steps result)))
    result))

(defn run-experiment [setting]
  (let [total (:total (:experiment setting))]
    (repeatedly total #(single-run setting))))

(defn calc-statistics
  [results]
  (let [wins-per-player (group-by :winner results)
        wins0 (count (wins-per-player 0))
        wins1 (count (wins-per-player 1))
        draws (count (wins-per-player -1))
        steps0 (double (if (zero? wins0) 0 (/ (reduce + (map :steps (wins-per-player 0))) wins0)))
        steps1 (double (if (zero? wins1) 0 (/ (reduce + (map :steps (wins-per-player 1))) wins1)))
        p0 (double (if (zero? (+ wins0 wins1)) 0 (/ (* 100 wins0) (+ wins0 wins1))))
        p1 (if (zero? (+ wins0 wins1)) 0.0 (- 100 p0))]
    (log/infof "Player 0 won %d times (%f%%), in %f steps average" wins0 p0 steps0)
    (log/infof "Player 1 won %d times (%f%%), in %f steps average" wins1 p1 steps1)
    (log/infof "Total %d draws (battle runs for too long)" draws)
    (log/infof "Total runs: %d" (+ wins0 wins1 draws))))

(defn -main [& args]
  (if (seq args)
    (let [f (first args)
          s (clojure.edn/read-string (slurp (format "setting/%s.edn" f)))
          results (run-experiment s)]
      (calc-statistics results))
    (printf "Please name current setting (e.g. baseline)."
            "Look for setting files in the \"setting\" dir."
            "\n**I cannot run without an argument**")))
