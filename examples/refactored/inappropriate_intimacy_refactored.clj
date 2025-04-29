(ns examples.refactored.inappropriate-intimacy-refactored)

(defprotocol SessionStore
  (read-session [this]))

(defrecord MemoryStore []
  SessionStore
  (read-session [_]
    {:user-id 42 :role "admin"}))

(defn get-session [store]
  (read-session store))

(def session-store (->MemoryStore))

(println (get-session session-store))