(ns examples.clojure-specific.smells.nested-forms)

(defn process [user]
  (let [profile (:profile user)]
    (when profile
      (let [address (:address profile)]
        (when address
          (let [city (:city address)]
            (str "City: " city)))))))

(process {:profile {:address {:city "Recife"}}})