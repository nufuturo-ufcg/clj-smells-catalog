(ns examples.traditional.refactored.middle-man-refactored)

(defn build-person [x]
  {:name (:name x) :age (:age x)})

(def people [{:name "Alice" :age 30}
             {:name "Bob" :age 25}])

(println (map build-person people))