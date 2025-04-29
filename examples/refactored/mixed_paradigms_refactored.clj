(ns examples.refactored.mixed-paradigms-refactored)

(defn make-counter []
  (atom 0))

(defn increment-counter [counter]
  (swap! counter inc))

(def counter (make-counter))

(println (increment-counter counter))
(println (increment-counter counter))