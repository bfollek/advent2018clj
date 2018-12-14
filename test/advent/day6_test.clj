(ns advent.day6-test
  (:require [clojure.test :refer :all]
            [advent.day6 :refer :all]))

(deftest test-day6-part1
  (testing "part1"
    (is (= 0 (part1 "data/day6.txt")))))