(ns examples.clojure-specific.smells.accessing-nonexistent-map-fields)

(defn welcome-message [user]
  (str "Welcome, " (:name user)))

(welcome-message {:id 42})
(welcome-message {:id 43 :name nil})