(ns examples.clojure-specific.smells.unnecessary-into)

(def users [{:id 1 :active true} {:id 2 :active false} {:id 3 :active true}])

(defn active-ids [users]
  (into [] (map :id (filter :active users))))

(defn id-set [users]
  (into #{} (map :id users)))

(defn rename-keys [m]
  (into {} (map (fn [[k v]] [(keyword (str "new-" (name k))) v]) m)))

(comment
  (active-ids users) ;; => [1 3]
  (id-set users)     ;; => #{1 2 3}
  (rename-keys {:a 1 :b 2}) ;; => {:new-a 1, :new-b 2}
  )