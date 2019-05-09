(ns advent.day01-test
  (:require [clojure.test :refer :all]
            [advent.day01 :refer :all]))

(deftest test-day01-part1
  (is (= 592 (sum-freqs "data/day01.txt"))))

(deftest test-day01-part2
  (is (= 241 (find-repeated-sum "data/day01.txt")))
  (is (= 241 (find-repeated-sum-reduce "data/day01.txt"))))