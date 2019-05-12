(ns advent.day05
  "Advent 2018 Day 5"
  (:require [clojure.string :as str]
            [rabbithole.core :as rh]))

(defn reaction?
  [c1 c2]
  ;; true if characters differ only in case.
  (and (not= c1 c2) (= (rh/to-lower c1) (rh/to-lower c2))))

(defn shrink
  ([s]
   (shrink s nil))
  ([s skip]
   (let [skip (rh/to-lower skip)
         skip-char? (fn [c] (= skip (rh/to-lower c)))
         ;; Drop any leading skip chars
         s (drop-while skip-char? s)]
     (reduce (fn [v c] (cond
                         (skip-char? c) v
                         ;; Skip c. v doesn't change.
                         (and (seq v) (reaction? (last v) c)) (pop v)
                         ;; Skip c and drop the last char of v.
                         ;; Keep v a vector so that conj works right.
                         ;; The (seq v) check makes sure v isn't empty. This comes up
                         ;; if the polymer starts with a reactive unit pair. It's removed,
                         ;; and (last v) is nil the next time through, and (reaction?)
                         ;; blows up if we call it.
                         ;;
                         ;; PERFORMANCE NOTES:
                         ;; First I tried (butlast) or (drop-last) wrapped in (vec) or (into []).
                         ;; They were about the same speed: more than twice as slow as (subvec).
                         ;; (pop) is about 10% faster (than subvec).
                         :else (conj v c)))
                         ;; Keep c
             [(first s)] (rest s)))))

(defn react-polymer
  [filename]
  (count (shrink (slurp filename))))

(defn improve-polymer
  [filename]
  (let [s (slurp filename)
        units (rh/char-range \a \z)]
    (->> units
         (map #(shrink s %))
         (map count)
         (apply min))))