(ns advent.day03-test
  (:require [clojure.test :refer :all]
            [advent.day03 :refer :all]))

(deftest test-day03-part1
  (is (= 109716 (multi-claimed-inches-of-fabric "data/day03.txt"))))

(deftest test-day03-part2
  (is (= 124 (no-overlap "data/day03.txt"))))