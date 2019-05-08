(ns advent.day01-test
  (:require [clojure.test :refer :all]
            [advent.day01 :refer :all]))

(deftest test-day01
  (testing "day01 from file"
    (is (= 592 (day01 "data/day01.txt")))))

(deftest test-day01-part2
  (testing "day01-part2 from file"
    (is (= 241 (day01-part2 "data/day01.txt")))))