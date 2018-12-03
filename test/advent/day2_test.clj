(ns advent.day2-test
  (:require [clojure.test :refer :all]
            [advent.day2 :refer :all]))

(deftest test-day2-part1
  (testing "checksum"
    (is (= 8715 (checksum "data/day2.txt")))))