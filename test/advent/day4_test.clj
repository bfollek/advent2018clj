(ns advent.day4-test
  (:require [clojure.test :refer :all]
            [advent.day4 :refer :all]))

(deftest test-day4-part1
  (testing "load-timestamps"
    (is (= 1045 (count (load-timestamps))))))
  ;;(testing "strategy-1"
    ;;(is (= ? (strategy-1)))))