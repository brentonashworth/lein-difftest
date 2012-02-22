(ns difftest.core-test
  (:use [clojure.test])
  (:require [difftest.core :as dt]))

(deftest diff?-test
  (is (false? (dt/diff? true)))
  (is (false? (dt/diff? '(not (= 1 2)))))
  (is (true? (dt/diff? '(not (= [1 2] [1 3])))))
  (is (true? (dt/diff? '(not (= {:a 1} {:a 2})))))
  (is (true? (dt/diff? '(not (= "ab" "ac"))))))

(deftest actual-diff-test
  (testing "booleans should not be diffed"
    (is (false? (dt/actual-diff false))))
  (testing "strings are diffed"
    (is (= (dt/actual-diff '(not (= "ab" "ac")))
           "  \"a\n - c\n + b\n   \"\n")))
  (testing "maps are diffed"
    (is (= (dt/actual-diff '(not (= {:a 1} {:a 2})))
             "  {:a\n - 2\n + 1\n   }\n"))))
