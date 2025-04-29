(def people
  [{:person/name "Fred"}
   {:person/name "Ethel"}
   {:person/name "Lucy"}])

(def collected-people
  (into {} (map (fn [p] [(:person/name p) p]) people)))

(println (contains? collected-people "Fred")) 
(println (contains? collected-people "Alice")) 