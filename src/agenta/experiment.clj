(ns agenta.experiment
  (:require agenta.core)
  (:import (agenta Engine GameMap SingleRandom)))

(defn single-run
  [max-ticks setting unit-types]
  (let [g (SingleRandom/get)
        m (GameMap. g)
        u (agenta.core/init-units g setting unit-types)
        e (Engine. m u)]
    (doseq [unit u] (.placeWherePossible m unit))
    (while (and (= -1 (.getWinner e))
                (< (.getTicks e) max-ticks))
      (.step e))
    (println (String/format "Player %d has won after %d ticks"
                            (object-array [(.getWinner e)
                                           (.getTicks e)])))
    {:winner (.getWinner e) :steps (.getTicks e)}))

(defn run-experiment
  [total-experiments max-ticks setting unit-types]
  (repeatedly total-experiments #(single-run max-ticks setting unit-types)))

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
