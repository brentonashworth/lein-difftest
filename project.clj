(defproject lein-difftest "1.3.1"
  :description "Run tests, display better test results."
  :dependencies [[org.clojure/clojure "1.2.0-RC3"]
                 [org.clojars.brenton/difform "1.1.0"]
                 [clj-stacktrace "0.1.2"]
                 [clansi "1.0.0"]]
  :dev-dependencies [[robert/hooke "1.0.1"]]
  :hooks [leiningen.hooks.difftest]
  :test-path "test-fail")
