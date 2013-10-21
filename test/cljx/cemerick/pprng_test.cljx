(ns cemerick.pprng-test
  #+cljs (:require-macros [cemerick.cljs.test :refer (deftest is are)])
  (:require [cemerick.pprng :as rng]
            #+cljs [cemerick.cljs.test :as t]
            #+clj [clojure.test :refer (deftest is are)]))

#+clj
(deftest appropriate-types
  (let [rng (rng/rng 42)]
    (are [fn type] (instance? type (fn rng))
         rng/int Integer
         rng/float Float
         rng/double Double
         rng/long Long
         rng/boolean Boolean)))

#+cljs
(deftest appropriate-types
  (let [rng (rng/rng 42)]
    (are [fn type] (number? (fn rng))
         rng/int 
         rng/float 
         rng/double 
         rng/long)
    (is (contains? #{true false} (rng/boolean rng)))))

#+cljs
(deftest sane-ranges
  (let [rng (rng/rng 42)]
    (doseq [[fn [low high]] {rng/int [-2147483648 2147483647]
                             rng/float [1.4E-45 3.4028235E38]
                             rng/double [4.9E-324 1.7976931348623157E308]
                             rng/long [-9007199254740992 9007199254740992]}]
      (dotimes [_ 100000]
        ; hardly conclusive, but maybe a reasonable sanity check of the ranges
        ; we're trying to enforce....however, *not* an actual test of the
        ; stretching we're doing to get to those ranges!
        (is (<= low (fn rng) high))))))

(deftest limited-ints
  (let [rng (rng/rng 42)]
    (dotimes [_ 100000]
      (let [limit (Math/abs (rng/int rng))
            int (rng/int rng limit)]
        (is (and (<= 0 int) (< int limit)))))))

#+clj
(deftest ju-random-compat
  (let [rng (java.util.Random.)]
    (are [fn] (not (nil? (fn rng)))
         rng/int rng/float rng/double rng/long rng/boolean)))

(deftest seed-availability
  (is (= 42 (-> 42 rng/rng rng/seed))))
