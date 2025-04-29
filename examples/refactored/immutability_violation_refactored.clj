(ns examples.refactored.immutability-violation-refactored)

(defn update-country [countries country]
  (assoc countries (:name country) country))

(let [countries (update-country {} {:name "Brazil" :pop 210})]
  (println countries))