(ns examples.smells.inappropriate-collection)

(def people
  [{:person/name "Fred"}
   {:person/name "Ethel"}
   {:person/name "Lucy"}])

(defn person-in-people?
  [person people]
  (some #(= person (:person/name %)) people))

(println (boolean (person-in-people? "Fred" people)))
(println (boolean (person-in-people? "Alice" people)))
