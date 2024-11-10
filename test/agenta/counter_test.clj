(ns agenta.counter-test
  (:require [clojure.test :refer :all]
            [agenta.counter :refer :all]))

(deftest next-val-test
  (testing "old counter ticks"
    (let [c (make 3)]
      (is (false? (-> c ready?)))
      (is (false? (-> c tick ready?)))
      (is (false? (-> c tick tick ready?)))
      (is (true?  (-> c tick tick tick ready?)))))

  (testing "new counter ticks"
    (let [c (make 3)]
      (is (false? (-> c ready?)))
      (is (false? (-> c next-val ready?)))
      (is (false? (-> c next-val next-val ready?)))
      (is (true?  (-> c next-val next-val next-val ready?))))))
