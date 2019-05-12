(ns advent.day05-test
  (:require [clojure.test :refer :all]
            [advent.day05 :refer :all]))

(deftest test-day05-part1
  (is (= 10450 (react-polymer "data/day05.txt"))))

(deftest test-day05-part2
  ;(testing "full file"
  ; (is (= 4624 (improve-polymer "data/day05.txt"))))
  (testing "short file"
    (is (= 140 (improve-polymer "data/day05-short.txt")))))