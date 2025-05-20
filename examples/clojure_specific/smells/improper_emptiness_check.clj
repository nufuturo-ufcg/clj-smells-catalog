(ns examples.clojure-specific.smells.improper-emptiness-check)

(defn process-if-not-empty [coll]
  (when (not (empty? coll))
    (str "Processing: " coll)))

(defn process-if-empty [coll]
  (when (= 0 (count coll))
    "Empty collection detected"))

[(process-if-not-empty [])
 (process-if-not-empty [1 2])
 (process-if-empty [])
 (process-if-empty [1])]