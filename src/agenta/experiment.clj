(ns agenta.experiment
  (:import (agenta Engine SingleRandom)
           (agenta.experiment RunResult)))

(defn single-run
  [max-ticks unit-types player0-units player1-units]
  (let [e (Engine. (SingleRandom/get) player0-units player1-units)]
    (.init e unit-types)
    (while (and (= -1 (.getWinner e))
                (< (.getTicks e) max-ticks))
      (.step e))
    (println (String/format "Player %d has won after %d ticks"
                            (object-array [(.getWinner e)
                                           (.getTicks e)])))
    (RunResult. (.getWinner e) (.getTicks e))))

(defn run-experiment
  [total-experiments max-ticks unit-types player0-units player1-units]
  (repeatedly total-experiments #(single-run max-ticks unit-types player0-units player1-units)))

(defn calc-statistics
  [results]
  (let [wins-per-player (group-by #(.-winner %) results)
        wins0 (count (wins-per-player 0))
        wins1 (count (wins-per-player 1))
        draws (count (wins-per-player -1))
        steps0 (if (zero? wins0) 0 (double (/ (reduce + (map #(.-steps %) (wins-per-player 0))) wins0)))
        steps1 (if (zero? wins1) 0 (double (/ (reduce + (map #(.-steps %) (wins-per-player 1))) wins1)))
        p0 (if (zero? (+ wins0 wins1)) 0 (double (/ (* 100 wins0) (+ wins0 wins1))))
        p1 (if (zero? (+ wins0 wins1)) 0 (- 100 p0))]
    (printf "Player 0 won %d times (%f%%), in %f steps average%n" wins0 p0 steps0)
    (printf "Player 1 won %d times (%f%%), in %f steps average%n" wins1 p1 steps1)
    (printf "Total %d draws (battle runs for too long)%n" draws)
    (printf "Total runs: %d%n" (+ wins0 wins1 draws))))
