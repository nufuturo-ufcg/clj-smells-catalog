(ns examples.traditional.refactored.shotgun-surgery-refactored)

(defn make-user [{:keys [name email age]}]
  {:name name
   :email email
   :age age})

(defn get-name [user] (:name user))
(defn get-email [user] (:email user))
(defn get-age [user] (:age user))

(defn save-user [user]
  (println "Saving to DB:" (get-name user) (get-email user) (get-age user)))

(defn send-welcome-email [user]
  (println "Sending email to:" (get-email user)))

(defn track-user [user]
  (println "Tracking new user:" (get-name user) (get-age user)))

(defn create-user [name email age]
  (let [user (make-user {:name name :email email :age age})]
    (save-user user)
    (send-welcome-email user)
    (track-user user)))

(comment
  (create-user "Alice" "alice@example.com" 30))