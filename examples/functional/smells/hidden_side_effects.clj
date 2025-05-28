(ns examples.functional.smells.hidden-side-effects)

(defn greet-user [user]
  ;; Hidden side effect: printing during map
  (println "Hello," (:name user))
  (str "Greeted " (:name user)))

(defn greet-users [users]
  (map greet-user users))

(let [users [{:name "Alice"} {:name "Bob"} {:name "Carol"}]]
  (greet-users users))