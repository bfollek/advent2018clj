(ns advent.day1)

(defn day1
  [file-name]
  (let [lines (-> file-name
                  slurp
                  (clojure.string/split #"\n"))
        nums (map #(Integer. %) lines)]
    (reduce + nums)))