(ns examples.clojure-specific.refactored.thread-ignorance-refactored)

(defn transform [xs]
  (->> xs
       (map inc)
       (filter even?)
       (reduce +)))

(transform [1 2 3 4])