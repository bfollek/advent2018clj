(ns advent.day02
  "Advent 2018 Day 2"
  (:require [clojure.string :as str]
            [rabbithole.core :as rh]))

(defn- check-id
  [m id]
  ; freqs is a seq of ints that are the char counts.
  ; e.g., for an id of "aabcdd", it's (2 1 1 2)
  (let [freqs (-> id frequencies vals)]
    (letfn [(update-cnt
              [key]
              (if (some #{key} freqs) (inc (m key)) (m key)))]
      ; Check the freqs seq for counts of 2 and 3.
      ; If we find a count, we increment the count of counts in m.
      ; e.g.; if m is {2 3, 3 1} and id is "aabcdd", the result
      ; is {2 4, 3 1} because we found a count of 2, but not 3.
      (hash-map 2 (update-cnt 2), 3 (update-cnt 3)))))

(defn checksum
  "Day 2, part1"
  [file-name]
  (let [ids (-> file-name slurp str/split-lines)
        m (reduce check-id {2 0, 3 0} ids)]
    (* (m 2) (m 3))))

(defn id-pairs
  ; Returns a collection of all possible pairs of id's.
  [file-name]
  (let [ids (-> file-name slurp str/split-lines)]
    (for [id1 ids id2 ids] [id1 id2])))

(defn diff-chars
  "Returns a map. The :diff key's value is a vector of the pairs of chars where s1 and s2 differ.
  The :same key's value is a vector of the pairs of chars that s1 and s2 have in common. Within
  each value vector, cach char pair is itself a vector."
  [s1 s2]
  ; Create pairs of (s1, s2) chars
  (let [char-pairs (rh/zip-up s1 s2)
        ; Separate pairs where the chars match from pairs where they don't
        m (group-by (fn [[x y]] (= x y)) char-pairs)]
    {:same (m true) :diff (m false)}))

(defn common-letters
  "Day 2, part2"
  [file-name]
  (->>
   (id-pairs file-name)
   (map (fn [[s1 s2]] (diff-chars s1 s2)))
   (filter #(= 1 (count (:diff %)))) ; s1 and s2 differ by just 1 char
   (first) ; first split-chars result that passes filter
   (:same) ; char pairs that are the same in s1 and s2
   (map first) ; first char in each pair
   (str/join))) ; to a string
