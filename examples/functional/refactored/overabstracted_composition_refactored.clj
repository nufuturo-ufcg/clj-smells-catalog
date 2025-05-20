(ns examples.functional.refactored.overabstracted-composition-refactored
  (:require
   [clojure.string :as str]))

(defn extract-domain [data]
  (let [email (get-in data [:user :email])
        cleaned (-> email str/trim str/lower-case)
        parts   (str/split cleaned #"@")]
    (second parts)))

(defn describe-user [data]
  (str "Domain: " (extract-domain data)))

(comment
  (describe-user {:user {:email "  Bob@Example.org  "}}))