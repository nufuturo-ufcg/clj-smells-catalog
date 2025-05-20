(ns examples.functional.refactored.deeply-nested-call-stacks-refactored
  (:require [clojure.string :as str]))

(defn sanitize [s]
  (-> s
      (clojure.string/replace #"[^a-zA-Z0-9]" "")
      clojure.string/lower-case
      clojure.string/trim))

(defn earliest-account-name [accounts]
  (:name (first (sort-by :created-at accounts))))

(defn process-user [user]
  (let [account-name (earliest-account-name (:accounts user))
        username (sanitize account-name)]
    (assoc user :username username)))

(def users [{:name "Alice"
             :accounts [{:created-at "2020-01-01" :name "Main"}]}
            {:name "Bob"
             :accounts [{:created-at "2019-05-01" :name "Legacy"}]}])

(println (map process-user users))