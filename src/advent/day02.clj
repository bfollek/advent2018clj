(ns advent.day02
  (:require [clojure.string :as str]
            [rabbithole.core :as rh]))

; --- Day 2: Inventory Management System ---
; You stop falling through time, catch your breath, and check the screen on the device. "Destination reached. Current Year: 1518. Current Location: North Pole Utility Closet 83N10." You made it! Now, to find those anomalies.

; Outside the utility closet, you hear footsteps and a voice. "...I'm not sure either. But now that so many people have chimneys, maybe he could sneak in that way?" Another voice responds, "Actually, we've been working on a new kind of suit that would let him fit through tight spaces like that. But, I heard that a few days ago, they lost the prototype fabric, the design plans, everything! Nobody on the team can even seem to remember important details of the project!"

; "Wouldn't they have had enough fabric to fill several boxes in the warehouse? They'd be stored together, so the box IDs should be similar. Too bad it would take forever to search the warehouse for two similar box IDs..." They walk too far away to hear any more.

; Late at night, you sneak to the warehouse - who knows what kinds of paradoxes you could cause if you were discovered - and use your fancy wrist device to quickly scan every box and produce a list of the likely candidates (your puzzle input).

; To make sure you didn't miss any, you scan the likely candidate boxes again, counting the number that have an ID containing exactly two of any letter and then separately counting those with exactly three of any letter. You can multiply those two counts together to get a rudimentary checksum and compare it to what your device predicts.

; For example, if you see the following box IDs:

; abcdef contains no letters that appear exactly two or three times.
; bababc contains two a and three b, so it counts for both.
; abbcde contains two b, but no letter appears exactly three times.
; abcccd contains three c, but no letter appears exactly two times.
; aabcdd contains two a and two d, but it only counts once.
; abcdee contains two e.
; ababab contains three a and three b, but it only counts once.
; Of these box IDs, four of them contain a letter which appears exactly twice, and three of them contain a letter which appears exactly three times. Multiplying these together produces a checksum of 4 * 3 = 12.

; What is the checksum for your list of box IDs?

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

; --- Part Two ---
; Confident that your list of box IDs is complete, you're ready to find the boxes full of prototype fabric.

; The boxes will have IDs which differ by exactly one character at the same position in both strings. For example, given the following box IDs:

; abcde
; fghij
; klmno
; pqrst
; fguij
; axcye
; wvxyz
; The IDs abcde and axcye are close, but they differ by two characters (the second and fourth). However, the IDs fghij and fguij differ by exactly one character, the third (h and u). Those must be the correct boxes.

; What letters are common between the two correct box IDs? (In the example above, this is found by removing the differing character from either ID, producing fgij.)

(defn id-pairs
  ; Returns a collection of all possible pairs of id's.
  [file-name]
  (let [ids (-> file-name slurp str/split-lines)]
    (for [id1 ids id2 ids] [id1 id2])))

(defn split-chars
  "Returns a two-item vector. The first item is a vector of the pairs of chars where s1 and s2 differ.
  The second item is a vector of the pairs of chars that s1 and s2 have in common. Each pair is a vector."
  ;Example: (split-chars "abc" "abd") => [[[\c \d]] [[\a \a] [\b \b]]]
  [s1 s2]
  ; Create pairs of (s1, s2) chars
  (let [char-pairs (rh/zip-up s1 s2)
        ; Separate pairs where the chars match from pairs where they don't
        m (group-by (fn [[x y]] (not= x y)) char-pairs)]
    [(m true) (m false)]))

(defn common-letters
  "Day 2, part2"
  [file-name]
  (->>
   (id-pairs file-name)
   (map (fn [[s1 s2]] (split-chars s1 s2)))
   (filter #(= 1 (count (first %))))
   (first) ; first split-chars result that passes filter
   (second) ; pairs of chars that s1 and s2 have in common
   (map first) ; first char in each pair
   (str/join))) ; to a string
