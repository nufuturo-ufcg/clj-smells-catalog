(ns examples.functional.smells.reinventing-the-wheel)

(defn process-data [data]
  (let [filtered (filter (fn [x] (not (nil? (get x :active)))) data)
        names (map (fn [x] (get x :name)) filtered)
        seconds (map (fn [x] (nth x 1)) (map vec (map :tags filtered)))
        flat-tags (apply concat (map (fn [x] (:tags x)) filtered))]
    {:names names
     :seconds seconds
     :flat-tags flat-tags}))

(process-data
 [{:name "Alice" :active true  :tags ["admin" "editor"]}
  {:name "Bob"   :active false :tags ["viewer" "editor"]}
  {:name "Carol" :active true  :tags ["editor" "reviewer"]}])