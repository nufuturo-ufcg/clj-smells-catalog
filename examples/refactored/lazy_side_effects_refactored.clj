(ns examples.refactored.lazy-side-effects-refactored)

(defn notify [x]
  (println "Notifying value:" x)
  x)

(def data (range 3))

(into []
      (comp
        (map notify)
        (filter even?))
      data)