(ns advent.day3-test
  (:require [clojure.test :refer :all]
            [advent.day3 :refer :all]))

(deftest test-day3-part1
  (testing "multi-claimed-inches-of-fabric"
    (is (= 109716 (multi-claimed-inches-of-fabric)))))