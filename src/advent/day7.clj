(ns advent.day7
  (:require [clojure.string :as str]
            [rabbithole.core :as rh]))

; --- Day 7: The Sum of Its Parts ---
; You find yourself standing on a snow-covered coastline; apparently, you landed a little off course. The region is too hilly to see the North Pole from here, but you do spot some Elves that seem to be trying to unpack something that washed ashore. It's quite cold out, so you decide to risk creating a paradox by asking them for directions.

; "Oh, are you the search party?" Somehow, you can understand whatever Elves from the year 1018 speak; you assume it's Ancient Nordic Elvish. Could the device on your wrist also be a translator? "Those clothes don't look very warm; take this." They hand you a heavy coat.

; "We do need to find our way back to the North Pole, but we have higher priorities at the moment. You see, believe it or not, this box contains something that will solve all of Santa's transportation problems - at least, that's what it looks like from the pictures in the instructions." It doesn't seem like they can read whatever language it's in, but you can: "Sleigh kit. Some assembly required."

; "'Sleigh'? What a wonderful name! You must help us assemble this 'sleigh' at once!" They start excitedly pulling more parts out of the box.

; The instructions specify a series of steps and requirements about which steps must be finished before others can begin (your puzzle input). Each step is designated by a single letter. For example, suppose you have the following instructions:

; Step C must be finished before step A can begin.
; Step C must be finished before step F can begin.
; Step A must be finished before step B can begin.
; Step A must be finished before step D can begin.
; Step B must be finished before step E can begin.
; Step D must be finished before step E can begin.
; Step F must be finished before step E can begin.
; Visually, these requirements look like this:


;   -->A--->B--
;  /    \      \
; C      -->D----->E
;  \           /
;   ---->F-----
; Your first goal is to determine the order in which the steps should be completed. If more than one step is ready, choose the step which is first alphabetically. In this example, the steps would be completed as follows:

; Only C is available, and so it is done first.
; Next, both A and F are available. A is first alphabetically, so it is done next.
; Then, even though F was available earlier, steps B and D are now also available, and B is the first alphabetically of the three.
; After that, only D and F are available. E is not available because only some of its prerequisites are complete. Therefore, D is completed next.
; F is the only choice, so it is done next.
; Finally, E is completed.
; So, in this example, the correct order is CABDFE.

; In what order should the steps in your instructions be completed?

(defn found-step
  "Found-step maintains the `steps->waiting-for` map by adding keys and values as necessary.
   Each key is a step name, and each value is a coll of step names that the
   key step is waiting for. Dups in the waiting-for coll are harmless. When
   a step finishes, we'll remove all occurences of the name."
  ([steps->waiting-for name]
   (found-step steps->waiting-for name nil))
  ([steps->waiting-for name waiting-for]
   (cond
     ;; Entry exists, waiting-for has a value - add it
     (and (steps->waiting-for name) waiting-for) (update steps->waiting-for name #(conj % waiting-for))
     ;; No entry - create entry with/without waiting-for value
     (not (steps->waiting-for name)) (assoc steps->waiting-for name (if waiting-for [waiting-for] []))
     ;; Else return steps->waiting-for we started with - nothing to change
     :else steps->waiting-for)))

(defn parse-step
  [steps->waiting-for s]
  (let [[_ step1 step2] (re-find #"Step\s+(\w)\s+must be finished before step\s+(\w)\s+can begin." s)]
    [step1 step2]
    (-> steps->waiting-for
        (found-step step1)
        (found-step step2 step1))))

(defn find-next
  [steps->waiting-for]
  (->> steps->waiting-for
       (filter (comp empty? val))
       keys
       sort
       first))

(defn update-step
  [steps->waiting-for step-name finished-step-name]
  (println "step-name:" step-name "finished-step-name:" finished-step-name)
  (update steps->waiting-for step-name (remove (partial = finished-step-name) (steps->waiting-for step-name))))

(defn finished-step
  [steps->waiting-for finished-step-name]
  ;; Remove the step we finished...
  (let [steps->waiting-for (dissoc steps->waiting-for finished-step-name)]
    ;; And remove it from the waiting-for coll in any other steps
    ;; (into {} (for [[k v] steps->waiting-for] [k (remove (partial = finished-step-name) v)]))))
    (reduce (fn [m k] (update m k (fn [waiting-for] (remove (partial = finished-step-name) waiting-for))))
            steps->waiting-for (keys steps->waiting-for))))

(defn part1
  [filename]
  (let [steps->waiting-for (reduce parse-step {} (rh/read-lines filename))]
    (loop [steps->waiting-for steps->waiting-for ordered-step-names []]
      (if (empty? steps->waiting-for)
        (apply str ordered-step-names) ; Done
        (let [next-step-name (find-next steps->waiting-for)]
          (recur (finished-step steps->waiting-for next-step-name)
                 (conj ordered-step-names next-step-name)))))))
