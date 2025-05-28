(ns examples.functional.smells.explicit-recursion)

(defn double-nums [nums]
  (if (empty? nums)
    '()
    (cons (* 2 (first nums)) (double-nums (rest nums)))))

(double-nums [1 2 3 4])