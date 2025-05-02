(ns examples.refactored.unnecessary-abstraction)

(defn greet-user [data]
  (str "Hello, " (get-in data [:user :name])))

(comment
  (greet-user {:user {:name "Charlie"}}))