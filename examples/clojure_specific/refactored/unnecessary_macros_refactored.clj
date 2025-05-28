(ns examples.clojure-specific.refactored.unnecessary-macros-refactored)

(defn unless-fn [test & body]
  (when (not test)
    (doseq [expr body] expr)))

(unless-fn false
           (println "This runs because test is false"))