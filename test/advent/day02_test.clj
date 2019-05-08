(ns advent.day02-test
  (:require [clojure.test :refer :all]
            [advent.day02 :refer :all]))

(deftest test-day02-part1
  (testing "checksum"
    (is (= 8715 (checksum "data/day02.txt")))))

(deftest test-day02-part2
  (testing "common-letters"
    (is (= "fvstwblgqkhpuixdrnevmaycd" (common-letters "data/day02.txt")))))
