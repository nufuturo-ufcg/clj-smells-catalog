(ns examples.smells.underutilizing-clojure-features)

(defn duplicate-and-wrap [x]
  [(str "<" x ">") (str "<" x ">")])

(def values ["a" "b" "c"])

(println (apply concat (map duplicate-and-wrap values)))