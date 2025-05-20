(ns examples.clojure-specific.smells.namespaced-keys-neglect)

(def user {:id 1 :name "Alice"})
(def order {:id 101 :name "Order-101"})

(println (:id user))    ;; 1
(println (:id order))   ;; 101