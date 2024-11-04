(ns agenta.engine-test
  (:require [clojure.test       :refer :all]
            [agenta.counter     :refer [make ready?]]
            [agenta.engine      :refer :all]
            [agenta.game-map    :refer [make-map]]
            [agenta.random      :refer [init!]]))

(deftest produce-messages-test
  (init! 777)
  (let [setting     (clojure.edn/read-string (slurp "setting/smoke.edn"))
        test-map    (make-map setting)
        phase1      (fn [m [xy a]]
                        (when (< 10 (:id a))
                          {:sender (:id a) :receiver (:id a) :message {:think (ready? (:think-counter a))}}))
        phase2      (fn [m [xy a]]
                        (when (even? (:id a))
                          {:sender (:id a) :receiver (:id a) :message {:speed (ready? (:speed-counter a))}}))]
    (testing "single phase"
      (is (= [{:sender 16, :receiver 16, :message {:think true}}
              {:sender 15, :receiver 15, :message {:think true}}
              {:sender 11, :receiver 11, :message {:think true}}
              {:sender 18, :receiver 18, :message {:think true}}
              {:sender 19, :receiver 19, :message {:think true}}
              {:sender 13, :receiver 13, :message {:think true}}]
             (produce-messages test-map [[:think-counter phase1]]))))
    (testing "multiple phases"
      (is (= [{:sender 16, :receiver 16, :message {:think true}}
              {:sender 15, :receiver 15, :message {:think true}}
              {:sender 11, :receiver 11, :message {:think true}}
              {:sender 18, :receiver 18, :message {:think true}}
              {:sender 19, :receiver 19, :message {:think true}}
              {:sender 13, :receiver 13, :message {:think true}}
              {:sender 14, :receiver 14, :message {:speed true}}
              {:sender 6, :receiver 6, :message {:speed true}}
              {:sender 4, :receiver 4, :message {:speed true}}]
             (produce-messages test-map [[:think-counter phase1]
                                         [:speed-counter phase2]]))))))
