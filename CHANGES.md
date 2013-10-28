# changelog

## 0.0.2

* `cemerick.pprng/int` and `cemerick.pprng/long` now properly emit only
  integers.
* `cemerick.pprng/int` now accepts an optional upper bound on the range of
  returned value, matching the contract of `java.util.Random/int`: when
  provided, the returned value will be between `[0,limit)`.
