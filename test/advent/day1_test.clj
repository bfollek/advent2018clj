(ns advent.day1-test
  (:require [clojure.test :refer :all]
            [advent.day1 :refer :all]))

(deftest test-day1
  (testing "day1 from file"
    (is (= 592 (day1 "data/day1.txt")))))

(deftest test-day1-part2
  (testing "day1-part2 from file"
    (is (= 241 (day1-part2 "data/day1.txt")))))