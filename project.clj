(defproject lein-difftest "1.3.2-SNAPSHOT"
  :description "Run tests, display better test results."
  :dependencies [[org.clojars.brenton/difform "1.1.1"]
                 [clj-stacktrace "0.2.0" :exclusions [org.clojure/clojure]]
                 [clansi "1.0.0" :exclusions [org.clojure/clojure]]]
  :eval-in-leiningen true
  :hooks [leiningen.hooks.difftest]
  :test-path "test-fail")
