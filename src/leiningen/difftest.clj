(ns leiningen.difftest
  (:require [leiningen.test :as test]))

(defn difftest [project & args]
  (apply test/test (-> project
                       (update-in [:injections] conj
                                  '((ns-resolve (doto 'difftest.core
                                                  require) 'activate)))
                       (update-in [:dependencies] into {'difftest "1.3.8"}))
         args))