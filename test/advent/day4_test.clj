(ns advent.day4-test
  (:require [clojure.test :refer :all]
            [advent.day4 :refer :all]))

(deftest test-day4-part1
  (testing "strategy-1"
    (is (= 72925 (strategy-1)))))

(deftest test-day4-part2
  (testing "strategy-2"
    (is (= 49137 (strategy-2)))))