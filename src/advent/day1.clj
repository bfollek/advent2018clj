(ns advent.day1)

(defn day1
  [file-name]
  (with-open [rdr (clojure.java.io/reader file-name)]
    (->> rdr
         line-seq
         (map #(Integer/parseInt %))
         (reduce +))))