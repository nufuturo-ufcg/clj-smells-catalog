(defn format-name [user]
  (str (:first-name user) " " (:last-name user)))

(defn valid-age? [user]
  (>= (:age user) 18))

(defn send-notification [user]
  (println (str "User " (:full-name user) " is valid. Sending notification...")))

(defn process-user [user]
  (let [full-name (format-name user)
        user-data {:full-name full-name :status (if (valid-age? user) "Valid" "Invalid")}]
    (when (= "Valid" (:status user-data))
      (send-notification user-data))
    user-data))

(println (process-user {:first-name "Alice" :last-name "Smith" :age 22}))