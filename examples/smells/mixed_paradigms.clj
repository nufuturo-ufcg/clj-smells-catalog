(ns examples.smells.mixed-paradigms)

(defrecord Counter [value])

(defn make-counter []
  (->Counter (atom 0)))

(defn increment-counter [^Counter c]
  (swap! (:value c) inc))

(def counter (make-counter))

(println (increment-counter counter))
(println (increment-counter counter))