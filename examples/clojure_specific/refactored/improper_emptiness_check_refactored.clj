(ns examples.clojure-specific.refactored.improper-emptiness-check-refactored)

(defn process-if-not-empty [coll]
  (when (seq coll)
    (str "Processing: " coll)))

(defn process-if-empty [coll]
  (when (empty? coll)
    "Empty collection detected"))

[(process-if-not-empty [])
 (process-if-not-empty [1 2])
 (process-if-empty [])
 (process-if-empty [1])]