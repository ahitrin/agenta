(ns agenta.domain-test
  "Tests for the Counter class"
  (:require [clojure.test :refer [deftest is testing]])
  (:import [agenta.domain Counter]))

(deftest counter-tests
  (testing "Create Counter with Max Value"
    (let [counter (Counter. 5)]
      (is (false? (.isReady counter)))))

  (testing "Tick the Counter"
    (let [counter (Counter. 5)]
      (dotimes [_ 4]
        (.tick counter))
      (is (false? (.isReady counter)))))

  (testing "Counter reaches zero"
    (let [counter (Counter. 5)]
      (dotimes [_ 5]
        (.tick counter))
      (is (true? (.isReady counter)))))

  (testing "Counter overreaches zero"
    (let [counter (Counter. 5)]
      (dotimes [_ 6]
        (.tick counter))
      (is (true? (.isReady counter)))))

  (testing "Reset the Counter"
    (let [counter (Counter. 5)]
      (.tick counter) ; 1 tick
      (.tick counter) ; 2 ticks
      (.reset counter)
      (.tick counter) ; 3 ticks
      (is (false? (.isReady counter)))))

  (testing "Reset and Tick to Ready Again"
    (let [counter (Counter. 5)]
      (.tick counter) ; 1 tick
      (.tick counter) ; 2 ticks
      (.reset counter)
      (dotimes [_ 5]
        (.tick counter))
      (is (true? (.isReady counter)))))

  (testing "Create Counter with Initial Value"
    (let [counter (Counter. 3 5)]
      (is (false? (.isReady counter)))))

  (testing "Tick until zero with initial value"
    (let [counter (Counter. 3 5)]
      (dotimes [_ 3]
        (.tick counter))
      (is (true? (.isReady counter)))))

  (testing "Reset after ticking"
    (let [counter (Counter. 3 5)]
      (.tick counter) ; 1 tick
      (.tick counter) ; 2 ticks
      (.reset counter)
      (dotimes [_ 4]
        (.tick counter))
      (is (false? (.isReady counter))))))
