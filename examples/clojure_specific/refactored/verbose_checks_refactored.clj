(ns examples.clojure-specific.refactored.verbose-checks-refactored)

(defn number-type [n]
  (cond
    (zero? n) :zero
    (pos? n)  :positive
    (neg? n)  :negative))

(number-type 0)

(number-type 5)

(number-type -3)