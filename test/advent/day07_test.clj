(ns advent.day07-test
  (:require [clojure.test :refer :all]
            [advent.day07 :refer :all]))

(deftest test-day07-parse-step
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

(deftest test-day07-part1
  (testing "part1"
    (is (= (part1 "data/day07.txt") "HEGMPOAWBFCDITVXYZRKUQNSLJ"))))