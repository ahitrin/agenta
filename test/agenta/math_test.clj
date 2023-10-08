(ns agenta.math-test
  (:require [clojure.test :refer :all]
            [agenta.math :refer :all]))

(deftest vector-test
  (testing "point substraction and addition"
    (let [p (xy 4 5) p' (xy 2 3)]
      (is (= (v-> p' p) (v 2 2)))
      (is (= (v-> p p') (v -2 -2)))
      (is (= p' (xy+v p (v-> p p'))))
      (is (= (v 0 0) (v+v (v-> p p') (v-> p' p))))
      (is (= p (xy+v p (v 0 0))))))

  (testing "vector multiplication"
    (let [vc (v 4 5)]
      (is (= (v 8 10)    (v* 2 vc)))
      (is (= (v 0 0)     (v* 0 vc)))
      (is (= (v 2.0 2.5) (v* 0.5 vc))))))
