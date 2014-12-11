# pprng [![Travis CI status](https://api.travis-ci.org/cemerick/pprng.png?branch=master)](http://travis-ci.org/#!/cemerick/pprng/builds)

portable pseudo-random number generators for Clojure/ClojureScript

## "Installation"

Add to your [Leiningen](http://leiningen.org) `project.clj`:

```clojure
[com.cemerick/pprng "0.0.3"]
```

## Usage

**NOTE** pprng does not use cryptographically-secure random number generators,
either on Clojure/JVM or ClojureScript/JS.  _**DO NOT**_ use this library to
help implement or inform any cryptographically-sensitive algorithm.

```clojure
user=> (require '[cemerick.pprng :as rng])
nil
;; get a new RNG, optionally providing a seed
user=> (def rng (rng/rng))
#'user/rng
;; obtain values from it...
user=> (rng/int rng)
-1170105035
user=> (rng/int rng 1000)
267
user=> (rng/boolean rng)
false
user=> (rng/double rng)
0.6832234717598454
;; obtain the seed that was used to create it originally, if necessary
user=> (rng/seed rng)
1372270869019
```

## Need Help?

Ping `cemerick` on freenode irc or
[twitter](http://twitter.com/cemerick) if you have questions or would
like to contribute patches.

## License

Copyright ©2013-\* [Chas Emerick](http://cemerick.com) and other contributors.

Distributed under the Eclipse Public License, the same as Clojure.
Please see the `epl-v10.html` file at the top level of this repo.
