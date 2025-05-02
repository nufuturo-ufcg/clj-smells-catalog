(ns examples.smells.unnecessary-abstraction)

;; Unnecessarily abstracted function just to get a user's name
(defn extract-user [data] (:user data))

(defn extract-name [user] (:name user))

(defn get-user-name [data]
  (let [user (extract-user data)
        name (extract-name user)]
    name))

(defn greet-user [data]
  (str "Hello, " (get-user-name data)))

(comment
  (greet-user {:user {:name "Charlie"}}))