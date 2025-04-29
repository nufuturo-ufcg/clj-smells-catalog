(ns examples.refactored.primitive-obsession-refactored)

(defrecord Money [amount currency])

(defn format-price [^Money m]
  (format "%.2f %s" (:amount m) (:currency m)))

(println (format-price (->Money 9.99 "USD")))