(ns examples.functional.refactored.overuse-of-high-order-functions-refactored)

(defn process-data [data]
  (map #(* 4 %) data))

(println (process-data [1 2 3 4]))