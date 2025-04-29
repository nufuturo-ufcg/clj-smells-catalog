(ns examples.smells.long-parameter-list)

(defn create-user [first-name last-name age email address phone]
  {:first-name first-name
   :last-name last-name
   :age age
   :email email
   :address address
   :phone phone})

(println (create-user "Alice" "Smith" 30 "alice@example.com" "123 Main St" "555-1234"))