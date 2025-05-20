(ns examples.clojure-specific.refactored.accessing-nonexistent-map-fields-refactored)

(defn welcome-message [user]
  (if (contains? user :name)
    (str "Welcome, " (:name user))
    "Name not provided"))

[(welcome-message {:id 42})
 (welcome-message {:id 44 :name "Alice"})]