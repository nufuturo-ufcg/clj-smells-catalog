(ns examples.clojure-specific.smells.immutability-violation)

(def countries {})

(defn update-country [country]
  (def countries (assoc countries (:name country) country)))

(update-country {:name "Brazil" :pop 210})
(println countries)