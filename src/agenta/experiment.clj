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
    (println (String/format "Player %d has won after %d ticks%n"
                            (object-array [(.getWinner e)
                                           (.getTicks e)])))
    (RunResult. (.getWinner e) (.getTicks e))))

(defn run-experiment
  [total-experiments max-ticks unit-types player0-units player1-units]
  (repeatedly total-experiments #(single-run max-ticks unit-types player0-units player1-units)))
