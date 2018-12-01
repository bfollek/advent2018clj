(ns advent.day1)

(defn to-i
  [s]
  (Integer/parseInt s))

(defn day1
  [file-name]
  (with-open [rdr (clojure.java.io/reader file-name)]
    (reduce + (map to-i (line-seq rdr)))))