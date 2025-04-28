(defn build-person [x]
  {:name (:name x) :age (:age x)})

(defn build-persons [xs]
  (map build-person xs))

(def people [{:name "Alice" :age 30}
             {:name "Bob" :age 25}])

(println (build-persons people))