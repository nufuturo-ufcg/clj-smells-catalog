(ns examples.functional.smells.lazy-side-effects)

(defn notify [x]
  (println "Notifying value:" x)
  x)

(def data (range 3))

(->> data
     (map notify)
     (filter even?))