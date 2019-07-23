(ns advent.day04
  "Advent 2018 Day 4"
  (:require [clojure.string :as str]
            [rabbithole.core :as rh]))

(defrecord Nap [id fell-asleep woke-up])

(defn- new-minute-counters
  "Returns a vector of 60 zeros, one for each minute."
  []
  (vec (take 60 (repeat 0))))

(defn- found-id
  [ids->minutes id]
  (if (ids->minutes id)
    ids->minutes ; id already in map
    (assoc ids->minutes id (new-minute-counters)))) ; Add id to map, and init minute counters

(defn- nap-over
  "Increments the minute counter for each minute the guard was asleep.
  It returns the updated `ids->minutes` map."
  [ids->minutes parsed]
  (reduce #(update-in %1 [(:id parsed) %2] inc) ids->minutes (range (:fell-asleep parsed) (:woke-up parsed))))

(def ^:private fix-timestamp-field (comp rh/to-int second))

(defn- parse-naps
  "Parses out the `nap` records from the `timestamp` string. Returns a vector of Nap."
  [timestamps]
  (let [[_ naps]
        (reduce (fn [[nap naps] ts]
                  (if-let [id (fix-timestamp-field (re-find #"Guard #(\d+) begins shift" ts))]
                    [(map->Nap {:id id}) naps]
                    (if-let [fell-asleep (fix-timestamp-field (re-find #":(\d+)\] falls asleep" ts))]
                      [(assoc nap :fell-asleep fell-asleep) naps]
                      (if-let [woke-up (fix-timestamp-field (re-find #":(\d+)\] wakes up" ts))]
                        ;; One guard may take multiple naps on a shift, so reuse the Nap record to keep the id.
                        [nap (conj naps (assoc nap :woke-up woke-up))]
                        (throw (Exception. (str "Unexpected timestamp:")))))))
                [nil []]
                timestamps)]
    naps))

  ; (loop [tss timestamps
  ;        nap nil
  ;        naps []]
  ;   (let [ts (first tss)]
  ;     (if (empty? tss)
  ;       naps
  ;       (if-let [id (fix-timestamp-field (re-find #"Guard #(\d+) begins shift" ts))]
  ;         (recur (rest tss) (map->Nap {:id id}) naps)
  ;         (if-let [fell-asleep (fix-timestamp-field (re-find #":(\d+)\] falls asleep" ts))]
  ;           (recur (rest tss) (assoc nap :fell-asleep fell-asleep) naps)
  ;           (if-let [woke-up (fix-timestamp-field (re-find #":(\d+)\] wakes up" ts))]
  ;             ;; One guard may take multiple naps on a shift, so reuse the Nap record to keep the id.
  ;             (recur (rest tss) nap (conj naps (assoc nap :woke-up woke-up)))
  ;             (throw (Exception. (str "Unexpected timestamp:" ts))))))))))

; (defn- load-timestamps
;   "Loads the timestamp data from a text file.
;   It stores the data in the `ids->minutes` map. Each map key is a guard id.
;   Each map value is a vector of 60 ints. The vector counts how many times
;   the guard was asleep at each minute of the shift. The function returns
;   the `ids->minutes` map."
;   [file-name]
;   (->>
;    (sort (rh/read-lines file-name))
;    (reduce (fn [[ids->minutes parsed] timestamp]
;              (let [[ids->minutes parsed] (parse-timestamp ids->minutes parsed timestamp)]
;                [ids->minutes parsed]))
;            [{} nil])
;    first))

(defn- most-naps-total
  "Finds the guard who spent the most minutes napping, total.
  It rReturns the `ids->minutes` entry for this guard."
  [ids->minutes]
  ;; https://clojuredocs.org/clojure.core/max-key
  (apply max-key #(reduce + (val %)) ids->minutes))

(defn- most-naps-minute
  "Finds the minute that a given guard did the most napping.
  It returns the number of minutes and the index in the minutes vector.
  It reads the vector twice, but it's a short vector."
  [ids->minutes-entry]
  (let [minutes (val ids->minutes-entry)
        indexed (rh/zip-up minutes (range 0 (count minutes)))]
    (apply max-key first indexed)))

; (defn strategy-1
;   [file-name]
;   (let [entry (most-naps-total (load-timestamps file-name))]
;     (* (key entry) (second (most-naps-minute entry)))))

(defrecord Timestamp [guard-id time action])

(defn- make-timestamp
  [guard-id time action]
  {:pre  [(some? guard-id) (some? time) (some? action)]}
  (map->Timestamp {:guard-id guard-id :time (rh/to-int time) :action action}))

(def ^:private begins-shift-regex #":(\d+)\] Guard #(\d+) begins shift")
(def ^:private falls-asleep-regex #":(\d+)\] falls asleep")
(def ^:private wakes-up-regex #":(\d+)\] wakes up")

(defn- one-line->timestamp
  "Parses a line into a Timestamp record.
  :falls_asleep and :wakes_up lines don't have the guard ID. They 'inherit' it from
  the :begins_shift line. So, dDepending on the line, we get the guard ID from either
  the line or the param."
  [line guard-id]
  ;; Lines:
  ;; [1518-11-01 00:00] Guard #10 begins shift
  ;; [1518-11-01 00:05] falls asleep
  ;; [1518-11-01 00:25] wakes up
  ;; [1518-11-01 00:30] falls asleep
  ;; [1518-11-01 00:55] wakes up
  (if-let [[_ time guard-id] (re-find begins-shift-regex line)]
    (make-timestamp guard-id time :begins_shift)
    (if-let [[_ time] (re-find falls-asleep-regex line)]
      (make-timestamp guard-id time :falls_asleep)
      (if-let [[_ time] (re-find wakes-up-regex line)]
        (make-timestamp guard-id time :wakes_up)
        (throw (Exception. (str "Unexpected line:" line)))))))

(defn- all-lines->timestamps
  [lines]
  (reduce (fn [[timestamps current-id] line]
            (let [nxt (one-line->timestamp line current-id)]
              [(conj timestamps nxt) (:guard-id nxt)]))
          [[] nil] lines))

(defn strategy-1-new
  [file-name]
  (->
   (rh/read-lines file-name)
   sort
   parse-naps))
   ;; text lines to vector of Timestamp records
   ;;all-lines->timestamps))

; (defn strategy-2
;   [file-name]
;   (let [most (->> (load-timestamps file-name)
;                   ;; A seq of vectors. Each vector is # of minutes, index of minutes, id of guard.
;                   (map #(conj (most-naps-minute %) (key %)))
;                   (apply max-key first))]
;     (* (second most) (last most))))