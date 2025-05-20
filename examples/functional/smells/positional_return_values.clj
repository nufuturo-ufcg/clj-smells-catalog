(ns examples.functional.smells.positional-return-values)

(defn sieve
  [p xs]
  [(filter p xs) (remove p xs)])

(first (sieve even? (range 9)))