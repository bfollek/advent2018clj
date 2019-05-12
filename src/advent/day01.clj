(ns advent.day01
  "Advent 2018 Day 1"
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [rabbithole.core :as rh]))

(defn sum-freqs
  "Each line of the input file `file-name` is an integer. Sum them all."
  [file-name]
  (->>
   (rh/read-lines file-name)
   (map rh/to-int)
   (reduce +)))

(defn- freq-changes
  "We may cycle through the input frequences more than once before we get an answer. Provide this."
  [file-name]
  (->>
   (rh/read-lines file-name)
   (map rh/to-int)
   cycle))

(defn find-repeated-sum-loop
  "Find the first frequency sum that repeats."
  [file-name]
  (loop [freq-sum 0 changes (freq-changes file-name) seen #{}] ; seen is a set
    (let [freq-sum (+ freq-sum (first changes))]
      (if (seen freq-sum)
        freq-sum ; Done - we've seen it before
        (recur freq-sum (rest changes) (conj seen freq-sum))))))

(defn- reduce-func
  "Given the sum of the frequences, a set of sums already seen, and the next frequency:
    Stop if we've seen the new sum already;
    Add the new sum to the set of sums already seen if we haven't seen it before."
  [[freq-sum seen] nxt-freq]
  (let [freq-sum (+ freq-sum nxt-freq)]
    (if (seen freq-sum)
      (reduced freq-sum) ; Done - we've seen it before
      [freq-sum (conj seen freq-sum)])))

(defn find-repeated-sum-reduce
  "Find the first frequency sum that repeats. This version uses `reduce` instead of `loop`."
  [file-name]
  (reduce reduce-func [0 #{}] (freq-changes file-name)))