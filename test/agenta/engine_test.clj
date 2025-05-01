(ns agenta.engine-test
  (:require [clojure.test :refer :all]
            [agenta.engine :refer :all]
            [agenta.counter :refer [make]]))

(deftest tick-health-test
  (testing "unit healt counter decreased after tick"
    (let [objs {[0 0] {:name "Fooer"
                       :health-counter (make 4 5)
                       :speed-counter (make 4 5)
                       :attack-counter (make 4 5)
                       :think-counter (make 4 5)
                       :health 10
                       :max-health 10}}
          target {[0 0] {:name "Fooer"
                         :health-counter (make 3 5)
                         :speed-counter (make 3 5)
                         :attack-counter (make 3 5)
                         :think-counter (make 3 5)
                         :health 10
                         :max-health 10}}]
      (is (= target (tick-health objs)))))
  (testing "counter that reaches zero should be reset after tick"
    (let [objs {[1 1] {:name "Barry"
                       :health-counter (make 1 5)
                       :speed-counter (make 1 5)
                       :attack-counter (make 1 5)
                       :think-counter (make 1 5)
                       :health 9
                       :max-health 10}}
          target {[1 1] {:name "Barry"
                         :health-counter (make 5 5)
                         :speed-counter (make 0 5)
                         :attack-counter (make 0 5)
                         :think-counter (make 0 5)
                         :health 10
                         :max-health 10}}]
      (is (= target (tick-health objs))))))
