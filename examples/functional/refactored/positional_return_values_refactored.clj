(ns examples.functional.refactored.positional-return-values-refactored)

(defn sieve
  [p xs]
  {:true (filter p xs) :false (remove p xs)})

(:true (sieve even? (range 9)))