(ns advent.day5-test
  (:require [clojure.test :refer :all]
            [advent.day5 :refer :all]))

(deftest test-day5-part1
  (testing "shrink"
    (is (= 10450 (shrink)))))