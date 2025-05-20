(ns examples.traditional.smells.comments
  (:require [clojure.string :as str]))

(defn save-user [user]
  ;; simulate saving to database
  (println "Saving user:" user))

(defn process-user [user]
  ;; validate input
  (when-not (:email user)
    (throw (Exception. "Missing email")))
  (when-not (:id user)
    (throw (Exception. "Missing ID")))

  ;; transform data
  (let [username (str/lower-case (:email user))
        uid (str "user-" (:id user))]

    ;; store in database
    (save-user {:username username
                :uid uid
                :email (:email user)})))

(process-user {:id 1 :email "Exemplo@Email.com"})