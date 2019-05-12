(ns advent.day03
  "Advent 2018 Day 3"
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [rabbithole.core :as rh]))

(defrecord Claim [id x y x-len y-len])

(defn string-to-claim
  [s]
  (->> s
       (re-find #"#(\d+) @ (\d+),(\d+): (\d+)x(\d+)")
       rest
       (map rh/to-int)
       (apply ->Claim)))

(defn read-claims
  [file-name]
  (let [lines (rh/read-lines file-name)]
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
  [file-name]
  (->> (read-claims file-name)
       (reduce map-claim {})     ; Map each Claim point to a vector of Claim ids
       vals                      ; Get the vectors of ids
       (filter #(> (count %) 1)) ; Get the vectors with more than one id
       count))                   ; Count them

(defn no-overlap
  [file-name]
  (let [claims (read-claims file-name)
        m (reduce map-claim {} claims)        ; Map each Claim point to a vector of Claim ids
        id-vecs (vals m)                      ; The id vectors from the map
        m (group-by #(= (count %) 1) id-vecs) ; point ids that are unique vs. point ids that are NOT unique
        unique-ids (rh/flat-set (m true))
        non-unique-ids (rh/flat-set (m false))]
    ;; ids can be in both collections. The one difference is the answer.
    (first (set/difference unique-ids non-unique-ids))))
