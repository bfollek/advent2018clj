(ns advent.day05-test
  (:require [clojure.test :refer :all]
            [advent.day05 :refer :all]))

(deftest test-day05-part1
  (testing "part1"
    (is (= 10450 (part1 "data/day05.txt")))))

(deftest test-day05-part2
  (testing "part2"
    ;;(is (= 4624 (part2 "data/day05.txt")))))
    (is (= 140 (part2 "data/day05-short.txt")))))