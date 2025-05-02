(ns examples.refactored.message-chains-refactored)

(def user
  {:profile {:contact {:email "user@example.com"}}})

(defn user-profile [user]
  (:profile user))

(defn user-contact [profile]
  (:contact profile))

(defn user-email [contact]
  (:email contact))

(defn get-user-email [user]
  (-> user
      user-profile
      user-contact
      user-email))

(println (get-user-email user))