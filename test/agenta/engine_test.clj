(ns agenta.engine-test
  (:require [clojure.test :refer :all]
            [agenta.engine :refer :all]
            [agenta.counter :as ctr]))

(deftest tick-health-test
  (testing "unit healt counter decreased after tick"
    (let [objs {[0 0] {:name "Fooer" :health-counter (ctr/make 4 5)}
                [1 1] {:name "Barry" :health-counter (ctr/make 2 5)}}
          target {[0 0] {:name "Fooer" :health-counter (ctr/make 3 5)}
                  [1 1] {:name "Barry" :health-counter (ctr/make 1 5)}}]
      (is (= target (tick-health objs)))))
  (testing "counter that reaches zero should be reset after tick"
    (let [objs {[0 0] {:name "Fooer" :health-counter (ctr/make 4 5)}
                [1 1] {:name "Barry" :health-counter (ctr/make 1 5)}}
          target {[0 0] {:name "Fooer" :health-counter (ctr/make 3 5)}
                  [1 1] {:name "Barry" :health-counter (ctr/make 5 5)}}
          ]
      (is (= target (tick-health objs))))))
