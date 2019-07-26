(ns advent.day04
  "Advent 2018 Day 4"
  (:require [clojure.string :as str]
            [rabbithole.core :as rh]))

(defrecord Nap [id fell-asleep woke-up])


(def ^:private new-minute-counters
  "A vector of 60 zeros, one for each minute."
  (vec (take 60 (repeat 0))))

(defn- ensure-id-in-map
  [ids->minutes id]
  (if (ids->minutes id)
    ids->minutes ; id already in map
    (assoc ids->minutes id new-minute-counters))) ; Add id to map, and init minute counters

(defn- count-nap
  "Increments the minute counter for each minute the guard was asleep.
  It returns the updated `ids->minutes` map."
  [ids->minutes nap]
  (reduce #(update-in %1 [(:id nap) %2] inc) ids->minutes (range (:fell-asleep nap) (:woke-up nap))))

(def ^:private fix-timestamp-field (comp rh/to-int second))

(defn- build-nap
  "Helper func for the (reduce) in `parse-naps`. Assembles the fields of a Nap record, and adds the record to the naps vector.

  Sample lines:

  [1518-11-01 00:00] Guard #10 begins shift
  [1518-11-01 00:05] falls asleep
  [1518-11-01 00:25] wakes up
  [1518-11-01 00:30] falls asleep
  [1518-11-01 00:55] wakes up

  :falls_asleep and :wakes_up lines don't have the guard ID. They 'inherit' it from the :begins_shift line.
  "
  [[nap naps] timestamp]
  (if-let [id (fix-timestamp-field (re-find #"Guard #(\d+) begins shift" timestamp))]
    [(map->Nap {:id id}) naps]
    (if-let [fell-asleep (fix-timestamp-field (re-find #":(\d+)\] falls asleep" timestamp))]
      [(assoc nap :fell-asleep fell-asleep) naps]
      (if-let [woke-up (fix-timestamp-field (re-find #":(\d+)\] wakes up" timestamp))]
        ;; We have a complete Nap record. Add it to the naps vector.
        ;; One guard may take multiple naps on a shift, so reuse the Nap record to keep the id.
        [nap (conj naps (assoc nap :woke-up woke-up))]
        (throw (Exception. (str "Unexpected timestamp:")))))))

(defn- parse-naps
  "Parses out the `nap` records from the `timestamp` string. Returns a vector of Nap."
  [timestamps]
  (let [[_ naps] (reduce build-nap [nil []] timestamps)]
    naps))

(defn- map-naps
  "Loads the Nap records into the `ids->minutes` map. Each map key is a guard id.
   Each map value is a vector of 60 ints. The vector counts how many times
   the guard was asleep at each minute of the shift.

   Returns the `ids->minutes` map."
  [naps]
  (reduce (fn [ids->minutes nap]
            (let [ids->minutes (ensure-id-in-map ids->minutes (:id nap))]
              (count-nap ids->minutes nap)))
          {} naps))

(defn- most-naps-total
  "Finds the guard who spent the most minutes napping, total.
  It returns the `ids->minutes` slot for this guard."
  [ids->minutes]
  ;; https://clojuredocs.org/clojure.core/max-key
  (apply max-key #(reduce + (val %)) ids->minutes))

(defn- most-naps-minute
  "Finds the minute that a given guard did the most napping.
  It returns the number of minutes and the index in the minutes vector.
  It reads the vector twice, but it's a short vector."
  [slot]
  (let [minutes (val slot)
        indexed (rh/zip-up minutes (range 0 (count minutes)))]
    (apply max-key first indexed)))

(defn- load-timestamps
  "Loads the timestamp data from a text file into a map.
   Returns the map."
  [file-name]
  (->
   (rh/read-lines file-name)
   sort
   parse-naps
   map-naps))

(defn strategy-1
  "Find the guard that has the most minutes asleep.
  What minute does that guard spend asleep the most?

  What is the ID of the guard you chose multiplied by the minute you chose?"
  [file-name]
  (let [slot (->> (load-timestamps file-name)
                  most-naps-total)]
    (* (key slot) (second (most-naps-minute slot)))))

(defn strategy-2
  "Of all guards, which guard is most frequently asleep on the same minute?

  What is the ID of the guard you chose multiplied by the minute you chose?"
  [file-name]
  (let [most (->> (load-timestamps file-name)
                  ; Make a seq of vectors. Each vector is # of minutes, index of minutes, id of guard.
                  (map #(conj (most-naps-minute %) (key %)))
                  (apply max-key first))]
    (* (second most) (last most))))