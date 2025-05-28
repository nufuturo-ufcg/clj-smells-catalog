(ns examples.functional.refactored.explicit-recursion-refactored)

(defn double-nums [nums]
  (map #(* 2 %) nums))

(double-nums [1 2 3 4])