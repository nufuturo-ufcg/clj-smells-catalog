(ns examples.refactored.comments-refactored
  (:require [clojure.string :as str]))

(defn save-user [user]
  (println "Saving user:" user))

(defn validate-user [user]
  (when-not (:email user)
    (throw (Exception. "Missing email")))
  (when-not (:id user)
    (throw (Exception. "Missing ID")))
  user)

(defn transform-user [user]
  {:username (str/lower-case (:email user))
   :uid (str "user-" (:id user))
   :email (:email user)})

(defn process-user [user]
  (-> user
      validate-user
      transform-user
      save-user))

(process-user {:id 1 :email "Exemplo@Email.com"})
