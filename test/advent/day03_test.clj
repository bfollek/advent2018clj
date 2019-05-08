(ns advent.day03-test
  (:require [clojure.test :refer :all]
            [advent.day03 :refer :all]))

(deftest test-day03-part1
  (testing "multi-claimed-inches-of-fabric"
    (is (= 109716 (multi-claimed-inches-of-fabric)))))

(deftest test-day03-part2
  (testing "no-overlap"
    (is (= 124 (no-overlap)))))