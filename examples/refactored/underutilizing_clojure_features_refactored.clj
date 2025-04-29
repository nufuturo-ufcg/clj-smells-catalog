(ns examples.refactored.underutilizing-clojure-features-refactored)

(defn duplicate-and-wrap [x]
  [(str "<" x ">") (str "<" x ">")])

(def values ["a" "b" "c"])

(println (mapcat duplicate-and-wrap values))