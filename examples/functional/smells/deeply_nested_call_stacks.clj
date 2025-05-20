(ns examples.functional.smells.deeply-nested-call-stacks
  (:require [clojure.string :as str]))

(defn sanitize [s]
  (clojure.string/trim (clojure.string/lower-case (clojure.string/replace s #"[^a-zA-Z0-9]" ""))))

(defn process-user [user]
  (assoc user :username (sanitize (:name (first (sort-by :created-at (:accounts user)))))))

(def users [{:name "Alice"
             :accounts [{:created-at "2020-01-01" :name "Main"}]}
            {:name "Bob"
             :accounts [{:created-at "2019-05-01" :name "Legacy"}]}])

(println (map process-user users))