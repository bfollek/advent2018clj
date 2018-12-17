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


(deftest test-day7-time-steps
  (testing "time-steps"
    (is (= 61 (time-steps "A")))
    (is (= 86 (time-steps "Z")))
    (is (= 147 (time-steps "A" "Z")))))
