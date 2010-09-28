(ns difftest.test-core
  (:require [difftest.core :as dt] :reload-all)
  (:use [clojure.test]))

(deftest test-actual-diff
  ;; If you have an assertion like (is (and foo bar)) the actual result comes
  ;; back as a single Boolean value, not a sequential form.
  (testing "should not call first on Boolean"
    (dt/actual-diff false)))
