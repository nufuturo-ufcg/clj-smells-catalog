(ns examples.smells.message-chains)

(def user
  {:profile {:contact {:email "user@example.com"}}})

(defn get-user-email [user]
  (-> user :profile :contact :email))

(println (get-user-email user))