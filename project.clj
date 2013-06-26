(defproject com.cemerick/pprng "0.0.1-SNAPSHOT"
  :jar-exclusions [#"\.cljx|\.swp|\.swo|\.DS_Store"]
  :source-paths ["src/cljx"]
  :resource-paths ["src/resources"]
  :test-paths ["target/test-classes"]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-1835"]]

  :cljx {:builds [{:source-paths ["src/cljx"]
                   :output-path "target/classes"
                   :rules :clj}

                  {:source-paths ["src/cljx"]
                   :output-path "target/classes"
                   :rules :cljs}

                  {:source-paths ["test/cljx"]
                   :output-path "target/test-classes"
                   :rules :clj}

                  {:source-paths ["test/cljx"]
                   :output-path "target/test-classes"
                   :rules :cljs}]}

  :cljsbuild {:test-commands {"phantom" ["runners/phantomjs.js" "target/testable.js"]}
              :builds [{:source-paths ["target/classes" "target/test-classes"
                                       "src/resources"]
                        :compiler {:output-to "target/testable.js"
                                   :libs [""]
                                   :optimizations :advanced
                                   :pretty-print true}}]}

  :profiles {:dev {:dependencies [[com.cemerick/clojurescript.test "0.0.4"]
                                  [com.cemerick/piggieback "0.0.5-SNAPSHOT"]
                                  [org.clojars.cemerick/cljx "0.3.0-SNAPSHOT"]]
                   :plugins [[org.clojars.cemerick/cljx "0.3.0-SNAPSHOT"]
                             ; temp fix https://github.com/emezeske/lein-cljsbuild/issues/210
                             [org.clojars.cemerick/lein-cljsbuild "0.3.2.1"]]
                   :aliases {"cleantest" ["do" "clean," "cljx" "once," "test,"
                                          "cljsbuild" "once," "cljsbuild" "test"]}
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl
                                                     cljx.repl-middleware/wrap-cljx]}
                   #_#_
                   :injections [(require '[cljs.repl.browser :refer (exec-env)]
                                         '[cemerick.piggieback :refer (cljs-repl)])
                                (defn browser-repl []
                                  (cljs-repl :repl-env
                                             ; requires fix for http://dev.clojure.org/jira/browse/CLJS-521
                                             (doto (exec-env :libs [""])
                                               ; TODO https://github.com/cemerick/piggieback/issues/10
                                               cljs.repl/-setup)))]}})

