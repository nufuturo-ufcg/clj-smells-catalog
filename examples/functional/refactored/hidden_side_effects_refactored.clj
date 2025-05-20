(ns examples.functional.refactored.hidden-side-effects-refactored)

(defn greet-user! [user]
  ;; Side effect now explicit and named
  (println "Hello," (:name user)))

(defn greet-users! [users]
  ;; Use doseq for side effects
  (doseq [user users]
    (greet-user! user)))

(let [users [{:name "Alice"} {:name "Bob"} {:name "Carol"}]]
  (greet-users! users))