(defn create-user [user-info]
  {:first-name (:first-name user-info)
   :last-name (:last-name user-info)
   :age (:age user-info)
   :email (:email user-info)
   :address (:address user-info)
   :phone (:phone user-info)})

(println (create-user {:first-name "Alice" 
                       :last-name "Smith" 
                       :age 30 
                       :email "alice@example.com" 
                       :address "123 Main St" 
                       :phone "555-1234"}))