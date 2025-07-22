(ns examples.functional.smells.overabstracted-composition 
  (:require
   [clojure.string :as str]))

(defn get-user [data] (:user data))
(defn get-email [user] (:email user))
(defn trim [s] (str/trim s))
(defn lower [s] (str/lower-case s))
(def domain (comp second #(str/split % #"@")))

;; Compose everything into a pipeline
(def process-email
  (comp
   domain
   lower
   trim
   get-email
   get-user))

(defn describe-user [data]
  (str "Domain: " (process-email data)))

(comment
  (describe-user {:user {:email "  Bob@Example.org  "}}))
