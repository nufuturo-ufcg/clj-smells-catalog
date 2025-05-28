(ns examples.functional.refactored.inefficient-filtering-refactored)

(require '[clojure.test.check.generators :as gen])

(def gen-even-int
  (gen/fmap #(* 2 %) (gen/choose 0 500)))

(println (gen/sample gen-even-int 5))