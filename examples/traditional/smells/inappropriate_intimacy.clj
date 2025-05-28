(ns examples.traditional.smells.inappropriate-intimacy)

(def session
  (proxy [clojure.lang.IDeref] []
    (deref []
      {:user-id 42 :role "admin"})
    (store []
      {:type ::memory-store})))

(println @session)
