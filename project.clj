(defproject com.cemerick/pprng "0.0.2-SNAPSHOT"
  :jar-exclusions [#"\.cljx|\.swp|\.swo|\.DS_Store"]
  :source-paths ["src/cljx"]
  :resource-paths ["src/resources"]
  :test-paths ["target/test-classes"]
  :dependencies [[org.clojure/clojure "1.6.0-alpha1"]
                 [org.clojure/clojurescript "0.0-2005"]]

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

  :cljsbuild {:test-commands {"phantom" ["phantomjs" :runner "target/testable.js"]}
              :builds [{:source-paths ["target/classes" "target/test-classes"
                                       "src/resources"]
                        :compiler {:output-to "target/testable.js"
                                   :libs [""]
                                   :optimizations :advanced
                                   :pretty-print true}}]}

  :profiles {:dev {:plugins [[com.cemerick/clojurescript.test "0.1.0"]
                             [com.keminglabs/cljx "0.3.1"]
                             [com.cemerick/austin "0.1.2-SNAPSHOT"]
                             [lein-cljsbuild "0.3.4"]]
                   :aliases {"cleantest" ["do" "clean," "cljx" "once," "test,"
                                          "cljsbuild" "test"]
                             "deploy" ["do" "clean," "cljx" "once," "deploy" "clojars"]}}})

