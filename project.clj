(defproject lein-difftest "1.3.7"
  :description "Run tests, display better test results."
  :dependencies [[difform "1.1.2"]
                 [clj-stacktrace "0.2.4"]
                 [clansi "1.0.0"]]
  :eval-in-leiningen true
  :hooks [leiningen.hooks.difftest])
