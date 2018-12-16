(ns advent.day7-test
  (:require [clojure.test :refer :all]
            [advent.day7 :refer :all]))

(deftest test-day7-parse-step
  (testing "parse-step - no dups"
    (is (= {"B" #{} "D" #{} "E" #{"B" "D" "F"} "F" #{}}
           (-> {}
               (parse-step "Step B must be finished before step E can begin.")
               (parse-step "Step D must be finished before step E can begin.")
               (parse-step "Step F must be finished before step E can begin.")))))
  (testing "parse-step - dups should have no effect"
    (is (= {"B" #{} "D" #{} "E" #{"B" "D" "F"} "F" #{}}
           (-> {}
               (parse-step "Step B must be finished before step E can begin.")
               (parse-step "Step B must be finished before step E can begin.")
               (parse-step "Step D must be finished before step E can begin.")
               (parse-step "Step D must be finished before step E can begin.")
               (parse-step "Step D must be finished before step E can begin.")
               (parse-step "Step F must be finished before step E can begin.")
               (parse-step "Step F must be finished before step E can begin."))))))