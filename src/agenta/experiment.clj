(ns agenta.experiment
  (:require [agenta.engine :as eng]))

(defn single-run [setting]
  (let [result (eng/run setting (fn [m u] ()))]
    (println (format "Player %d has won after %d ticks" (:winner result) (:steps result)))
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
        steps0 (if (zero? wins0) 0 (double (/ (reduce + (map :steps (wins-per-player 0))) wins0)))
        steps1 (if (zero? wins1) 0 (double (/ (reduce + (map :steps (wins-per-player 1))) wins1)))
        p0 (if (zero? (+ wins0 wins1)) 0 (double (/ (* 100 wins0) (+ wins0 wins1))))
        p1 (if (zero? (+ wins0 wins1)) 0 (- 100 p0))]
    (printf "Player 0 won %d times (%f%%), in %f steps average%n" wins0 p0 steps0)
    (printf "Player 1 won %d times (%f%%), in %f steps average%n" wins1 p1 steps1)
    (printf "Total %d draws (battle runs for too long)%n" draws)
    (printf "Total runs: %d%n" (+ wins0 wins1 draws))))

(defn -main [& args]
  (if (seq args)
    (let [f (first args)
          s (clojure.edn/read-string (slurp (format "setting/%s.edn" f)))
          results (run-experiment s)]
      (calc-statistics results))
    (println "Please name current setting (e.g. baseline)."
             "Look for setting files in the \"setting\" dir."
             "\n**I cannot run without an argument**")))
