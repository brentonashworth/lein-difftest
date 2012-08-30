(ns leiningen.difftest
  (:require [leiningen.test :as test]
            [leiningen.core.project :as project]))

(def profile
  {:injections ['((ns-resolve (doto 'difftest.core require) 'activate))]
   :dependencies [['difftest "1.3.8"]]})

(defn difftest
  "Run tests with improved failure output."
  [project & args]
  (apply test/test (project/merge-profiles project [profile]) args))