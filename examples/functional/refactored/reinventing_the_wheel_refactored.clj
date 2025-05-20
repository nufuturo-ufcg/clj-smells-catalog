(ns examples.functional.refactored.reinventing-the-wheel-refactored)

(defn process-data [data]
  (let [filtered (filter :active data)
        names (map :name filtered)
        seconds (map second (map :tags filtered))
        flat-tags (mapcat :tags filtered)]
    {:names names
     :seconds seconds
     :flat-tags flat-tags}))

(process-data
 [{:name "Alice" :active true  :tags ["admin" "editor"]}
  {:name "Bob"   :active false :tags ["viewer" "editor"]}
  {:name "Carol" :active true  :tags ["editor" "reviewer"]}])