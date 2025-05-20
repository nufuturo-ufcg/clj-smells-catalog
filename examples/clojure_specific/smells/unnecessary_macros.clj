(ns examples.clojure-specific.smells.unnecessary-macros)

(defmacro unless [test & body]
  `(if (not ~test)
     (do ~@body)))

(unless false
        (println "This runs because test is false"))