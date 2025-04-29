(ns examples.smells.long-function)

(defn process-users [users]
  (let [filtered (filter #(> (:age %) 18) users)
        formatted (map #(str (:first-name %) " " (:last-name %)) filtered)
        report (map #(hash-map :full-name %1 :age (:age %2)) formatted filtered)]
    report))

(println (process-users [{:first-name "Alice" :last-name "Smith" :age 22}
                         {:first-name "Bob" :last-name "Johnson" :age 17}
                         {:first-name "Charlie" :last-name "Brown" :age 25}])) 