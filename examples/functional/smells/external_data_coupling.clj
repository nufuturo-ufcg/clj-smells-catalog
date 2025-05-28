(ns examples.functional.smells.external-data-coupling)

;; Raw external data received from an API or other system
(def raw-user-data
  {:user_name "alice"
   :user_age 30
   :user_email "alice@example.com"})

(defn process-user [raw-user]
  (println "Welcome," (:user_name raw-user))
  (println "Your email is:" (:user_email raw-user))
  (if (> (:user_age raw-user) 18)
    (println "You are an adult.")
    (println "You are a minor.")))

(comment
  (process-user raw-user-data))