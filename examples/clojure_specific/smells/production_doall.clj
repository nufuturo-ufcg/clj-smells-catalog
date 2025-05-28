(ns examples.clojure-specific.smells.production-doall)

(defn print-evens []
  (doall (map #(println %) (filter even? (range 1000)))))

(print-evens)