(ns examples.functional.smells.trivial-lambda)

(defn square [x]
  (* x x))

(def numbers [1 2 3 4])

(println (map #(square %) numbers))