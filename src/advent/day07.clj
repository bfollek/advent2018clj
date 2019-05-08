(ns advent.day07
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

; --- Part Two ---

; As you're about to begin construction, four of the Elves offer to help. "The sun will set soon; it'll go faster if we work together." Now, you need to account for multiple people working on steps simultaneously. If multiple steps are available, workers should still begin them in alphabetical order.

; Each step takes 60 seconds plus an amount corresponding to its letter: A=1, B=2, C=3, and so on. So, step A takes 60+1=61 seconds, while step Z takes 60+26=86 seconds. No time is required between steps.

; To simplify things for the example, however, suppose you only have help from one Elf (a total of two workers) and that each step takes 60 fewer seconds (so that step A takes 1 second and step Z takes 26 seconds). Then, using the same instructions as above, this is how each second would be spent:

; Second   Worker 1   Worker 2   Done
;    0        C          .
;    1        C          .
;    2        C          .
;    3        A          F       C
;    4        B          F       CA
;    5        B          F       CA
;    6        D          F       CAB
;    7        D          F       CAB
;    8        D          F       CAB
;    9        D          .       CABF
;   10        E          .       CABFD
;   11        E          .       CABFD
;   12        E          .       CABFD
;   13        E          .       CABFD
;   14        E          .       CABFD
;   15        .          .       CABFDE
; Each row represents one second of time. The Second column identifies how many seconds have passed as of the beginning of that second. Each worker column shows the step that worker is currently doing (or . if they are idle). The Done column shows completed steps.

; Note that the order of the steps has changed; this is because steps now take time to finish and multiple workers can begin multiple steps simultaneously.

; In this example, it would take 15 seconds for two workers to complete these steps.

; With 5 workers and the 60+ second step durations described above, how long will it take to complete all of the steps?

(defn found-step
  "Found-step maintains the `steps` map by adding keys and values as necessary.
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

(defn part1
  [filename]
  (let [steps (load-steps filename)]
    (loop [steps steps ordered-step-names []]
      (if (empty? steps)
        (str/join ordered-step-names) ; Done
        (let [next-step-name (find-next steps 1)]
          (recur (finished-steps steps next-step-name)
                 (apply conj ordered-step-names next-step-name)))))))

; (defn time-steps
;   [step-names]
;   {:pre  [(seq step-names)]}
;   (letfn [(valu [step-name]
;             ;; "A" = 61, "B" = 62, etc.
;             (+ 61 (- (int (first step-name)) (int \A))))]
;     (reduce + (map valu step-names))))

; (defn part2
;   [filename num-workers]
;   (let [steps (load-steps filename)]
;     (loop [steps steps clock 0 num-working 0]
;       (if (empty? steps)
;         clock ; Done
;         (let [next-step-names (find-next steps (- num-workers num-working))]
;           (recur (finished-steps steps next-step-names)
;                  ;; Would have to deal with num-working here
;                  ;; Also steps still in progress
;                  (+ clock (time-steps next-step-names))
;                  (num-workers))))))
