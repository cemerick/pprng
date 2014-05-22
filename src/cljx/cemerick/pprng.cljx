(ns cemerick.pprng
  #+cljs (:require math.seedrandom
                   [cljs.core :as lang])
  #+clj (:require [clojure.core :as lang])
  #+clj (:import java.util.Random)
  (:refer-clojure :exclude (double float int long boolean)))

(require '[clojure.core.typed :as tc
           ])

#+clj (set! *warn-on-reflection* true)

#_(tc/ann-protocol IRandom
  -seed [IRandom -> tc/AnyInteger]
  -next-double [IRandom -> Double]
  -next-float [IRandom -> Float]
  -next-int (Fn [IRandom -> Integer] [IRandom Integer -> Integer])
  -next-long [IRandom -> Long]
  -next-boolean [IRandom -> Boolean])

; this protocol warns about reflection under Clojure < 1.6.0
; http://dev.clojure.org/jira/browse/CLJ-1202
(tc/defprotocol IRandom
  (-seed [this] :- tc/AnyInteger)
  (-next-double [this] :- Double)
  (-next-float [this] :- Float)
  (-next-int
    [this] :- Integer
    [this ^long limit :- Integer] :- Integer)
  (-next-long [this] :- Long)
  (-next-boolean [this] :- Boolean))

(tc/ann-record SeededRandom [seed :- tc/AnyInteger
                             rng :- java.util.Random])

#+clj
(defrecord SeededRandom [seed ^Random rng]
  IRandom
  (-seed [this] seed)
  (-next-double [this] (.nextDouble rng))
  (-next-float [this] (.nextFloat rng))
  (-next-int [this] (.nextInt rng))
  (-next-int [this limit] (.nextInt rng limit))
  (-next-long [this] (.nextLong rng))
  (-next-boolean [this] (.nextBoolean rng)))

#+clj
(extend-protocol IRandom
  Random
  (-seed [this]
    (throw (IllegalArgumentException.
             (str "Cannot obtain original seed directly from a java.util.Random instance.  "
                  "Use `"*ns* "/rng` to always obtain a RNG that retains its original seed value."))))
  (-next-double [this] (.nextDouble this))
  (-next-float [this] (.nextFloat this))
  (-next-int
    ([this] (.nextInt this))
    ([this limit] (.nextInt this limit)))
  (-next-long [this] (.nextLong this))
  (-next-boolean [this] (.nextBoolean this)))

#+cljs
(defn- between
  [random low high]
  (+ low (* (random) (- high low))))

#+cljs
(defrecord SeededRandom [seed random-double]
  IRandom
  (-seed [this] seed)
  (-next-double [this] (random-double))
  (-next-float [this] (between random-double 1.4E-45 3.4028235E38))
  ; imprecise, but should be reliably so
  (-next-int [this] (lang/long (between random-double -2147483648 2147483647)))
  (-next-int [this limit] (lang/long (between random-double 0 limit)))
  (-next-long [this] (lang/long (between random-double -9007199254740992 9007199254740992)))
  (-next-boolean [this] (zero? (Math/floor (* 2 (random-double))))))


(tc/ann rng (Fn [-> IRandom]
              ; not true for JS
              [long -> IRandom]))
(defn rng
  "Returns a new random number generator seeded with [seed] that satisfies the
  `IRandom` protocol."
  ; TODO apply the same transformation to current time as j.u.Random does to
  ; obtain "a value very likely to be distinct from any other invocation" of
  ; this fn
  ([] (rng (.getTime #+clj (java.util.Date.) #+cljs (js/Date.))))
  ([seed]
   #+cljs (Math/seedrandom seed)
   (SeededRandom. seed
                  #+clj (Random. seed)
                  ; seedrandom bashes out Math/random; capture it so
                  ; later `rng` invocations won't mess with existing RNGs' state
                  #+cljs (do (Math/seedrandom seed)
                             Math/random))))

(tc/ann seed [IRandom -> tc/AnyInteger])
(defn seed
  "Returns the seed used to create the given RNG."
  [rng]
  (-seed rng))

(tc/ann double [IRandom -> Double])
(defn double
  "Returns the next double value from the given RNG."
  [rng]
  (-next-double rng))

(tc/ann float [IRandom -> Float])
(defn float
  "Returns the next float value from the given RNG."
  [rng]
  (-next-float rng))

(tc/ann int (Fn [IRandom -> Integer] [IRandom Integer -> Integer]))
(defn int
  "Returns the next int value from the given RNG.  If supplied a [limit] argument, the range of the returned int will be [0,limit)."
  ([rng] (-next-int rng))
  ([rng limit] (-next-int rng limit)))

(tc/ann long [IRandom -> Long])
(defn long
  "Returns the next long value from the given RNG."
  [rng]
  (-next-long rng))

(tc/ann boolean [IRandom -> Boolean])
(defn boolean
  "Returns the next boolean value from the given RNG."
  [rng]
  (-next-boolean rng))

