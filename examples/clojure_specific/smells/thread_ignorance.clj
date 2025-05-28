(ns examples.clojure-specific.smells.thread-ignorance)

(defn transform [xs]
  (let [step1 (map inc xs)
        step2 (filter even? step1)
        step3 (reduce + step2)]
    step3))

(transform [1 2 3 4])