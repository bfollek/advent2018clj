(ns advent.day04
  (:require [rabbithole.core :as rh]))

; --- Day 4: Repose Record ---
; You've sneaked into another supply closet - this time, it's across from the prototype suit manufacturing lab. You need to sneak inside and fix the issues with the suit, but there's a guard stationed outside the lab, so this is as close as you can safely get.

; As you search the closet for anything that might help, you discover that you're not the first person to want to sneak in. Covering the walls, someone has spent an hour starting every midnight for the past few months secretly observing this guard post! They've been writing down the ID of the one guard on duty that night - the Elves seem to have decided that one guard was enough for the overnight shift - as well as when they fall asleep or wake up while at their post (your puzzle input).

; For example, consider the following records, which have already been organized into chronological order:

; [1518-11-01 00:00] Guard #10 begins shift
; [1518-11-01 00:05] falls asleep
; [1518-11-01 00:25] wakes up
; [1518-11-01 00:30] falls asleep
; [1518-11-01 00:55] wakes up
; [1518-11-01 23:58] Guard #99 begins shift
; [1518-11-02 00:40] falls asleep
; [1518-11-02 00:50] wakes up
; [1518-11-03 00:05] Guard #10 begins shift
; [1518-11-03 00:24] falls asleep
; [1518-11-03 00:29] wakes up
; [1518-11-04 00:02] Guard #99 begins shift
; [1518-11-04 00:36] falls asleep
; [1518-11-04 00:46] wakes up
; [1518-11-05 00:03] Guard #99 begins shift
; [1518-11-05 00:45] falls asleep
; [1518-11-05 00:55] wakes up
; Timestamps are written using year-month-day hour:minute format. The guard falling asleep or waking up is always the one whose shift most recently started. Because all asleep/awake times are during the midnight hour (00:00 - 00:59), only the minute portion (00 - 59) is relevant for those events.

; Visually, these records show that the guards are asleep at these times:

; Date   ID   Minute
;             000000000011111111112222222222333333333344444444445555555555
;             012345678901234567890123456789012345678901234567890123456789
; 11-01  #10  .....####################.....#########################.....
; 11-02  #99  ........................................##########..........
; 11-03  #10  ........................#####...............................
; 11-04  #99  ....................................##########..............
; 11-05  #99  .............................................##########.....
; The columns are Date, which shows the month-day portion of the relevant day; ID, which shows the guard on duty that day; and Minute, which shows the minutes during which the guard was asleep within the midnight hour. (The Minute column's header shows the minute's ten's digit in the first row and the one's digit in the second row.) Awake is shown as ., and asleep is shown as #.

; Note that guards count as asleep on the minute they fall asleep, and they count as awake on the minute they wake up. For example, because Guard #10 wakes up at 00:25 on 1518-11-01, minute 25 is marked as awake.

; If you can figure out the guard most likely to be asleep at a specific time, you might be able to trick that guard into working tonight so you can have the best chance of sneaking in. You have two strategies for choosing the best guard/minute combination.

; Strategy 1: Find the guard that has the most minutes asleep. What minute does that guard spend asleep the most?

; In the example above, Guard #10 spent the most minutes asleep, a total of 50 minutes (20+25+5), while Guard #99 only slept for a total of 30 minutes (10+10+10). Guard #10 was asleep most during minute 24 (on two days, whereas any other minute the guard was asleep was only seen on one day).

; While this example listed the entries in chronological order, your entries are in the order you found them. You'll need to organize them before they can be analyzed.

; What is the ID of the guard you chose multiplied by the minute you chose? (In the above example, the answer would be 10 * 24 = 240.)

; # --- Part Two ---
; # Strategy 2: Of all guards, which guard is most frequently asleep on the same minute?

; # In the example above, Guard #99 spent minute 45 asleep more than any other guard or minute - three times in total. (In all other cases, any guard spent any minute asleep at most twice.)

; # What is the ID of the guard you chose multiplied by the minute you chose? (In the above example, the answer would be 99 * 45 = 4455.)

;; ########################################

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
  []
  (loop [lines (sort (rh/read-lines "data/day04.txt")) ids->minutes {} parsed nil]
    (if (empty? lines)
      ids->minutes
      (let [[ids->minutes parsed] (parse-timestamp ids->minutes parsed (first lines))]
        (recur (rest lines) ids->minutes parsed)))))

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
  []
  (let [entry (most-naps-total (load-timestamps))]
    (* (key entry) (second (most-naps-minute entry)))))

(defn strategy-2
  []
  (let [most (->> (load-timestamps)
                  ;; A seq of vectors. Each vector is # of minutes, index of minutes, id of guard.
                  (map #(conj (most-naps-minute %) (key %)))
                  (apply max-key first))]
    (* (second most) (last most))))