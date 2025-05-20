(ns examples.traditional.smells.primitive-obsession)

(defn format-price [amount currency]
  (format "%.2f %s" amount currency))

(println (format-price 9.99 "USD"))