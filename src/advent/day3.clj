(ns advent.day3
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [rabbithole.core :as rh]))

; --- Day 3: No Matter How You Slice It ---
; The Elves managed to locate the chimney-squeeze prototype fabric for Santa's suit (thanks to someone who helpfully wrote its box IDs on the wall of the warehouse in the middle of the night). Unfortunately, anomalies are still affecting them - nobody can even agree on how to cut the fabric.

; The whole piece of fabric they're working on is a very large square - at least 1000 inches on each side.

; Each Elf has made a claim about which area of fabric would be ideal for Santa's suit. All claims have an ID and consist of a single rectangle with edges parallel to the edges of the fabric. Each claim's rectangle is defined as follows:

; The number of inches between the left edge of the fabric and the left edge of the rectangle.
; The number of inches between the top edge of the fabric and the top edge of the rectangle.
; The width of the rectangle in inches.
; The height of the rectangle in inches.
; A claim like #123 @ 3,2: 5x4 means that claim ID 123 specifies a rectangle 3 inches from the left edge, 2 inches from the top edge, 5 inches wide, and 4 inches tall. Visually, it claims the square inches of fabric represented by # (and ignores the square inches of fabric represented by .) in the diagram below:

; ...........
; ...........
; ...#####...
; ...#####...
; ...#####...
; ...#####...
; ...........
; ...........
; ...........
; The problem is that many of the claims overlap, causing two or more claims to cover part of the same areas. For example, consider the following claims:

; #1 @ 1,3: 4x4
; #2 @ 3,1: 4x4
; #3 @ 5,5: 2x2
; Visually, these claim the following areas:

; ........
; ...2222.
; ...2222.
; .11XX22.
; .11XX22.
; .111133.
; .111133.
; ........
; The four square inches marked with X are claimed by both 1 and 2. (Claim 3, while adjacent to the others, does not overlap either of them.)

; If the Elves all proceed with their own plans, none of them will have enough fabric. How many square inches of fabric are within two or more claims?

(defrecord Claim [id x y x-len y-len])

(defn string-to-claim
  [s]
  (->> s
       (re-find #"#(\d+) @ (\d+),(\d+): (\d+)x(\d+)")
       rest
       (map rh/to-int)
       (apply ->Claim)))

(defn read-claims
  []
  (let [lines (rh/read-lines "data/day3.txt")]
    (map string-to-claim lines)))

;; Each point is a key into the map.
;; Each map value is an array of Claim ids that include the point.
(defn update-point
  [m point id]
  (update m point (fn [current] (if (nil? current) [id] (conj current id)))))

;; Generate all points the Claim covers, and add them to the map.
(defn map-claim
  [m c]
  (let [xs (range (:x c) (+ (:x c) (:x-len c)))
        ys (range (:y c) (+ (:y c) (:y-len c)))
        points (for [x xs y ys] [x y])]
    (reduce #(update-point %1 %2 (:id c)) m points)))

(defn multi-claimed-inches-of-fabric
  []
  (->> (read-claims)
       (reduce map-claim {})     ; Map each Claim point to a vector of Claim ids
       vals                      ; Get the vectors of ids
       (filter #(> (count %) 1)) ; Get the vectors with more than one id
       count))                   ; Count them

; Amidst the chaos, you notice that exactly one claim doesn't overlap by even a single square inch of fabric with any other claim. If you can somehow draw attention to it, maybe the Elves will be able to make Santa's suit after all!

; For example, in the claims above, only claim 3 is intact after all claims are made.

; What is the ID of the only claim that doesn't overlap?

(defn no-overlap
  []
  (let [claims (read-claims)
        m (reduce map-claim {} claims)                           ; Map each Claim point to a vector of Claim ids
        id-vecs (vals m)                                         ; The id vectors from the map
        just-1 (set (flatten (filter #(= (count %) 1) id-vecs))) ; ids that are alone on a point
        multi (set (flatten (filter #(> (count %) 1) id-vecs)))] ; ids that are NOT alone on a point
    (first (set/difference just-1 multi))))                      ; difference is the answer
