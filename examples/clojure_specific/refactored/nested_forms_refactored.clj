(ns examples.clojure-specific.refactored.nested-forms-refactored)

(defn process [user]
  (when-let [city (some-> user :profile :address :city)]
    (str "City: " city)))

(process {:profile {:address {:city "Recife"}}})