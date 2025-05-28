(ns examples.clojure-specific.smells.verbose-checks)

(defn number-type [n]
  (cond
    (= n 0) :zero
    (< 0 n) :positive
    (> 0 n) :negative))

(number-type 0)
;; => :zero

(number-type 5)
;; => :positive

(number-type -3)
;; => :negative