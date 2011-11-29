(defproject lein-difftest "1.3.6"
  :description "Run tests, display better test results."
  :dependencies [[org.clojure/clojure "1.2.1"]
                 [difform "1.1.2"]
                 [clj-stacktrace "0.2.3"]
                 [clansi "1.0.0"]]
  :eval-in-leiningen true
  :hooks [leiningen.hooks.difftest])
