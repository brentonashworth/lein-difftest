(ns leiningen.difftest
  (:require [leiningen.test :as test]))

(defn difftest-form [& args]
  `(do (require '~'difftest.core)
       ~(apply (.getRoot #'test/form-for-testing-namespaces) args)))

(defn difftest [project & args]
  (binding [test/form-for-testing-namespaces difftest-form]
    (apply test/test project args)))