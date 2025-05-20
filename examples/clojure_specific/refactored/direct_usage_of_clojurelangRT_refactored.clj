(ns examples.clojure-specific.refactored.direct-usage-of-clojurelangRT-refactored)

(defn print-all [xs]
  (doseq [x xs]
    (println x)))

(print-all [1 2 3])