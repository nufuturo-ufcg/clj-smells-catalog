(ns examples.clojure-specific.refactored.redundant-do-block-refactored)

(defn process-item [x]
  (when (pos? x)
    (println "Processing:" x)
    (* x 2)))

(process-item 2)