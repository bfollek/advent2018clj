(ns advent.day02-test
  (:require [clojure.test :refer :all]
            [advent.day02 :refer :all]))

(deftest test-day02-part1
  (is (= 8715 (checksum "data/day02.txt"))))

(deftest test-day02-part2
  (is (= "fvstwblgqkhpuixdrnevmaycd" (common-letters "data/day02.txt"))))

(deftest test-diff-chars
  (let [m (diff-chars "abc" "abd")]
    (is (and (= [[\c \d]] (:diff m)) (= [[\a \a] [\b \b]] (:same m))))))
