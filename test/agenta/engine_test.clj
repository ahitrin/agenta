(ns agenta.engine-test
  (:require [clojure.test       :refer :all]
            [agenta.counter     :refer [make ready?]]
            [agenta.engine      :refer :all]
            [agenta.game-map    :refer [make-map]]
            [agenta.random      :refer [init!]]))

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

(deftest produce-messages-test
  (init! 777)
  (let [setting     (clojure.edn/read-string (slurp "setting/smoke.edn"))
        test-map    (make-map setting)
        phase1      (fn [m [xy a]]
                        {:sender (:id a) :receiver (:id a) :message {:think (ready? (:think-counter a))}})
        phase2      (fn [m [xy a]]
                        {:sender (:id a) :receiver (:id a) :message {:speed (ready? (:speed-counter a))}})]
    (testing "single phase"
      (is (= [{:sender 16, :receiver 16, :message {:think true}}
              {:sender 15, :receiver 15, :message {:think true}}
              {:sender 8, :receiver 8, :message {:think true}}
              {:sender 11, :receiver 11, :message {:think true}}
              {:sender 18, :receiver 18, :message {:think true}}
              {:sender 19, :receiver 19, :message {:think true}}
              {:sender 7, :receiver 7, :message {:think true}}
              {:sender 0, :receiver 0, :message {:think true}}
              {:sender 13, :receiver 13, :message {:think true}}]
             (produce-messages test-map [[:think-counter phase1]]))))
    (testing "multiple phases"
      (is (= [{:sender 16, :receiver 16, :message {:think true}}
              {:sender 15, :receiver 15, :message {:think true}}
              {:sender 8, :receiver 8, :message {:think true}}
              {:sender 11, :receiver 11, :message {:think true}}
              {:sender 18, :receiver 18, :message {:think true}}
              {:sender 19, :receiver 19, :message {:think true}}
              {:sender 7, :receiver 7, :message {:think true}}
              {:sender 0, :receiver 0, :message {:think true}}
              {:sender 13, :receiver 13, :message {:think true}}
              {:sender 14, :receiver 14, :message {:speed true}}
              {:sender 9, :receiver 9, :message {:speed true}}
              {:sender 15, :receiver 15, :message {:speed true}}
              {:sender 3, :receiver 3, :message {:speed true}}
              {:sender 6, :receiver 6, :message {:speed true}}
              {:sender 4, :receiver 4, :message {:speed true}}]
             (produce-messages test-map [[:think-counter phase1]
                                         [:speed-counter phase2]]))))))
