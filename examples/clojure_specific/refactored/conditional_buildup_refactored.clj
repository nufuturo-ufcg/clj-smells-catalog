(ns examples.clojure-specific.refactored.conditional-buildup-refactored)

(defn f0 [in] (* in 10))
(defn f1 [in] (+ in 1))
(defn f2 [in] (- in 1))
(defn p1 [in] (pos? in))
(defn p2 [in] (even? in))

(defn foo
  [in]
  (cond-> {:k0 (f0 in)}
    (p1 in) (assoc :k1 (f1 in))
    (p2 in) (assoc :k2 (f2 in))))

(foo 2)