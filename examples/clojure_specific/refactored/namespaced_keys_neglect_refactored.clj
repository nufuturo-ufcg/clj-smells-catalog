(ns examples.clojure-specific.refactored.namespaced-keys-neglect-refactored)

(def user {:user/id 1 :user/name "Alice"})
(def order {:order/id 101 :order/name "Order-101"})

(println (:user/id user))    ;; 1
(println (:order/id order))  ;; 101