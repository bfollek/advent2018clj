(ns advent.day5-test
  (:require [clojure.test :refer :all]
            [advent.day5 :refer :all]))

(deftest test-day5-part1
  (testing "part1"
    (is (= 10450 (part1 "data/day5.txt")))))

(deftest test-day5-part2
  (testing "part2"
    ;;(is (= 4624 (part2 "data/day5.txt")))))
    (is (= 140 (part2 "data/day5-short.txt")))))