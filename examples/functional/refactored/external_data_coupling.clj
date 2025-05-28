(ns examples.functional.refactored.external-data-coupling)

(def raw-user-data
  {:user_name "alice"
   :user_age 30
   :user_email "alice@example.com"})

;; Transform external data into an internal model
(defn transform-user [external-user]
  {:name  (:user_name external-user)
   :age   (:user_age external-user)
   :email (:user_email external-user)})

(defn process-user [user]
  (println "Welcome," (:name user))
  (println "Your email is:" (:email user))
  (if (> (:age user) 18)
    (println "You are an adult.")
    (println "You are a minor.")))

(comment
  (let [user (transform-user raw-user-data)]
    (process-user user)))