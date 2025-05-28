(ns examples.clojure-specific.smells.direct-usage-of-clojurelangRT)

(defn print-all [xs]
  (let [it (clojure.lang.RT/iter xs)]
    (loop []
      (when (.hasNext it)
        (println (.next it))
        (recur)))))

(print-all [1 2 3])