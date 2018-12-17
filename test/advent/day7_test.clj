(ns advent.day7-test
  (:require [clojure.test :refer :all]
            [advent.day7 :refer :all]))

(deftest test-day7-parse-step
  (testing "parse-step - no dups"
    (is (= {"B" [] "D" [] "E" ["B" "D" "F"] "F" []}
           (-> {}
               (parse-step "Step B must be finished before step E can begin.")
               (parse-step "Step D must be finished before step E can begin.")
               (parse-step "Step F must be finished before step E can begin.")))))
  (testing "parse-step - dups are harmless"
    (is (= {"B" [] "D" [] "E" ["B" "B" "D" "D" "D" "F" "F"] "F" []}
           (-> {}
               (parse-step "Step B must be finished before step E can begin.")
               (parse-step "Step B must be finished before step E can begin.")
               (parse-step "Step D must be finished before step E can begin.")
               (parse-step "Step D must be finished before step E can begin.")
               (parse-step "Step D must be finished before step E can begin.")
               (parse-step "Step F must be finished before step E can begin.")
               (parse-step "Step F must be finished before step E can begin."))))))

(deftest test-day7-part1
  (testing "part1"
    (is (= (part1 "data/day7.txt") "HEGMPOAWBFCDITVXYZRKUQNSLJ"))))


(deftest test-day7-time-step
  (testing "time-step"
    (is (= 61 (time-step "A")))
    (is (= 86 (time-step "Z")))))
