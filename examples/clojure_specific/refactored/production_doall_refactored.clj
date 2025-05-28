(ns examples.clojure-specific.refactored.production-doall-refactored)

(defn print-evens []
  (doseq [n (filter even? (range 1000))]
    (println n)))

(print-evens)