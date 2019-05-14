(ns advent.day07
  "Advent 2018 Day 7"
  (:require [clojure.string :as str]
            [rabbithole.core :as rh]))

(defn found-step
  "Maintains the `steps` map by adding keys and values as necessary.
   Each key is a step name, and each value is a coll of step names that the
   key step is waiting for. Dups in the waiting-for coll are harmless. When
   a step finishes, we'll remove all occurences of the name."
  ([steps step-name]
   (found-step steps step-name nil))
  ([steps step-name waiting-for]
   (cond
     ;; Entry exists, waiting-for has a value - add it
     (and (steps step-name) waiting-for) (update steps step-name #(conj % waiting-for))
     ;; No entry - create entry with/without waiting-for value
     (not (steps step-name)) (assoc steps step-name (if waiting-for [waiting-for] []))
     ;; Else return steps we started with - nothing to change
     :else steps)))

(defn parse-step
  [steps s]
  (let [[_ step1 step2] (re-find #"Step\s+(\w)\s+must be finished before step\s+(\w)\s+can begin." s)]
    (-> steps
        (found-step step1)
        (found-step step2 step1))))

(defn load-steps
  [filename]
  (reduce parse-step {} (rh/read-lines filename)))
(defn ready-to-run
  [steps]
  (->> steps
       (filter (comp empty? val))
       keys
       sort))

(defn find-next
  [steps n]
  (->> steps
       (filter (comp empty? val))
       keys
       sort
       (take n)))

(defn finished-steps
  [steps finished-names]
  {:pre  [(seq finished-names)]}
  ;; Remove the step we finished...
  (let [steps (apply dissoc steps finished-names)]
    ;; And remove it from the waiting-for coll in any other steps
    (apply merge (for [[k v] steps] {k (remove (set finished-names) v)}))))

(defn time-to-complete-steps
  [filename]
  (let [steps (load-steps filename)]
    (loop [steps steps ordered-step-names []]
      (if (empty? steps)
        (str/join ordered-step-names) ; Done
        (let [next-step-name (find-next steps 1)]
          (recur (finished-steps steps next-step-name)
                 (apply conj ordered-step-names next-step-name)))))))
