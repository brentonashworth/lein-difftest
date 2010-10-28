(defproject lein-difftest "1.3.2-SNAPSHOT"
  :description "Run tests, display better test results."
  :dependencies [[org.clojars.brenton/difform "1.1.0"]
                 [clj-stacktrace "0.2.0" :exclusions [org.clojure/clojure]]
                 [clansi "1.0.0"]]
  :hooks [leiningen.hooks.difftest]
  :test-path "test-fail")
