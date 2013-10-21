(ns cemerick.pprng
  (:refer-clojure :exclude (double float int long boolean))
  #+clj (:import java.util.Random)
  #+cljs (:require math.seedrandom))

(defprotocol IRandom
  (-seed [this])
  (-next-double [this])
  (-next-float [this])
  (-next-int [this] [this limit])
  (-next-long [this])
  (-next-boolean [this]))

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
  (-next-int [this] (between random-double -2147483648 2147483647))
  (-next-int [this limit] (between random-double 0 limit))
  (-next-long [this] (between random-double -9007199254740992 9007199254740992))
  (-next-boolean [this] (zero? (Math/floor (* 2 (random-double))))))

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

(defn seed
  "Returns the seed used to create the given RNG."
  [rng]
  (-seed rng))

(defn double
  "Returns the next double value from the given RNG."
  [rng]
  (-next-double rng))

(defn float
  "Returns the next float value from the given RNG."
  [rng]
  (-next-float rng))

(defn int
  "Returns the next int value from the given RNG.  If supplied a [limit] argument, the range of the returned int will be [0,limit)."
  ([rng] (-next-int rng))
  ([rng limit] (-next-int rng limit)))

(defn long
  "Returns the next long value from the given RNG."
  [rng]
  (-next-long rng))

(defn boolean
  "Returns the next boolean value from the given RNG."
  [rng]
  (-next-boolean rng))

