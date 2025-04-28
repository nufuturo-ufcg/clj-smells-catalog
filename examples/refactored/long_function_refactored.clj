(defn is-adult? [user]
  (> (:age user) 18))

(defn format-user [user]
  {:full-name (str (:first-name user) " " (:last-name user))
   :age (:age user)})

(defn process-users [users]
  (map format-user (filter is-adult? users)))

(println (process-users [{:first-name "Alice" :last-name "Smith" :age 22}
                         {:first-name "Bob" :last-name "Johnson" :age 17}
                         {:first-name "Charlie" :last-name "Brown" :age 25}])) 