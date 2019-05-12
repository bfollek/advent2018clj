(ns advent.day04
  "Advent 2018 Day 4"
  (:require [rabbithole.core :as rh]))

(defrecord Parsed [id fell-asleep woke-up])

(defn new-minute-counters
  "New-minute-counters returns a vector of 60 zeros, one for each minute."
  []
  (vec (take 60 (repeat 0))))

(defn found-id
  [ids->minutes id]
  (if (ids->minutes id)
    ids->minutes ; id already in map
    (assoc ids->minutes id (new-minute-counters)))) ; Add id to map, and init minute counters

(defn nap-over
  "Nap-over increments the minute counter for each minute the guard was asleep.
  It returns the updated `ids->minutes` map."
  [ids->minutes parsed]
  (reduce #(update-in %1 [(:id parsed) %2] inc) ids->minutes (range (:fell-asleep parsed) (:woke-up parsed))))

(defn parse-timestamp
  "Parse-timestamp parses out the data from the `timestamp` string.
  It stores the data in the `parsed` record. It updates the `id->minutes`
  map as necessary. It returns the `id->minutes` map and the `parsed` record."
  [ids->minutes parsed timestamp]
  (let [fix-field (comp rh/to-int second)]
    (if-let [id (fix-field (re-find #"Guard #(\d+) begins shift" timestamp))]
      [(found-id ids->minutes id) (map->Parsed {:id id})]
      (if-let [fell-asleep (fix-field (re-find #":(\d+)\] falls asleep" timestamp))]
        [ids->minutes (assoc parsed :fell-asleep fell-asleep)]
        (if-let [woke-up (fix-field (re-find #":(\d+)\] wakes up" timestamp))]
          ;; One guard may take multiple naps on a shift, so the parsed record preserves the guard id.
          [(nap-over ids->minutes (assoc parsed :woke-up woke-up)) (assoc parsed :fell-asleep nil :woke-up nil)]
          (throw (Exception. (str "Unexpected timestamp:" timestamp))))))))

(defn load-timestamps
  "Load-timestamps loads the timestamp data from a text file.
  It stores the data in the `ids->minutes` map. Each map key is a guard id.
  Each map value is a vector of 60 ints. The vector counts how many times
  the guard was asleep at each minute of the shift. The function returns
  the `ids->minutes` map."
  [file-name]
  (->>
   (sort (rh/read-lines file-name))
   (reduce (fn [[ids->minutes parsed] timestamp]
             (let [[ids->minutes parsed] (parse-timestamp ids->minutes parsed timestamp)]
               [ids->minutes parsed]))
           [{} nil])
   first))

(defn most-naps-total
  "Most-naps-total finds the guard who spent the most minutes napping, total.
  It rReturns the `ids->minutes` entry for this guard."
  [ids->minutes]
  ;; https://clojuredocs.org/clojure.core/max-key
  (apply max-key #(reduce + (val %)) ids->minutes))

(defn most-naps-minute
  "Most-naps-minute finds the minute that a given guard did the most napping.
  It returns the number of minutes and the index in the minutes vector.
  It reads the vector twice, but it's a short vector."
  [ids->minutes-entry]
  (let [minutes (val ids->minutes-entry)
        indexed (rh/zip-up minutes (range 0 (count minutes)))]
    (apply max-key first indexed)))

(defn strategy-1
  [file-name]
  (let [entry (most-naps-total (load-timestamps file-name))]
    (* (key entry) (second (most-naps-minute entry)))))

(defn strategy-2
  [file-name]
  (let [most (->> (load-timestamps file-name)
                  ;; A seq of vectors. Each vector is # of minutes, index of minutes, id of guard.
                  (map #(conj (most-naps-minute %) (key %)))
                  (apply max-key first))]
    (* (second most) (last most))))