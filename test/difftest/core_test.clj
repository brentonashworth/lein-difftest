(ns difftest.core-test
  (:use [clojure.test])
  (:require [difftest.core :as dt]))

(deftest actual-diff-test
    (testing "booleans should not be diffed"
    (is (false? (dt/actual-diff false)))))
