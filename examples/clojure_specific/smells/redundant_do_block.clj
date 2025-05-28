(ns examples.clojure-specific.smells.redundant-do-block)

(defn process-item [x]
  (when (pos? x)
    (do
      (println "Processing:" x)
      (* x 2))))

(process-item 2)