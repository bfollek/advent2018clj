(ns advent.day04-test
  (:require [clojure.test :refer :all]
            [advent.day04 :refer :all]))

(deftest test-day04-part1
  (is (= 72925 (strategy-1 "data/day04.txt"))))

(deftest test-day04-part2
  (is (= 49137 (strategy-2 "data/day04.txt"))))