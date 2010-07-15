;; Copyright (c) Brenton Ashworth. All rights reserved.
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file COPYING at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.

(ns difftest.core
  (:use clansi)
  (:require [clojure.test :as ct]
            [clojure.stacktrace :as stack]
            [com.georgejahad.difform :as difform]
            [clj-stacktrace.core :as clj-stacktrace]
            [clj-stacktrace.repl]))

(defn difform-str
  "Create a string that is the diff of the forms x and y."
  [x y]
  (with-out-str
    (difform/clean-difform x y)))

(defn actual-diff
  "Transform the actual form that comes from clojure.test into a diff
   string. This will diff forms like (not (= ...)) and will return the string
   representation of anything else."
  [form]
  (cond (and (= (first form) 'not) (= (first (last form)) '=))
        (let [[_ [_ actual expected]] form]
          (difform-str expected
                       actual))
        :else form))

(defn get-testing-vars-str
  "Wrap clojure.test/testing-vars-str to make it compatible with Clojure 1.1
   and 1.2."
  [m]
  (try (ct/testing-vars-str)
       (catch Exception _ (ct/testing-vars-str m))))

;; Reporting functions copied from clojure.test and modified to diff
;; failures and use clj-stacktrace for errors.

(defmulti difftest-report :type)

(defmethod difftest-report :default [m]
  (ct/with-test-out (prn m)))

(defmethod difftest-report :pass [m]
  (ct/with-test-out (ct/inc-report-counter :pass)))

(defmethod difftest-report :fail [m]
  (ct/with-test-out
   (ct/inc-report-counter :fail)
   (println (style (str "\nFAIL in " (get-testing-vars-str m)) :bright))
   (when (seq ct/*testing-contexts*)
     (println
      (style (ct/testing-contexts-str) :bright)))
   (when-let [message (:message m)] (println (style message :bright)))
   (println "expected:" (pr-str (:expected m)))
   (println "  actual:\n" (let [actual (:actual m)]
                            (actual-diff actual)))))

(defmethod difftest-report :error [m]
  (ct/with-test-out
   (ct/inc-report-counter :error)
   (println (style (str "\nERROR in " (get-testing-vars-str m)) :bright :red))
   (when (seq ct/*testing-contexts*) (println (ct/testing-contexts-str)))
   (when-let [message (:message m)] (println message))
   (println "expected:" (pr-str (:expected m)))
   (print " actual: ")
   (let [actual (:actual m)]
     (if (instance? Throwable actual)
       (println (clj-stacktrace.repl/pst-str actual))
       (prn actual)))))

(defmethod difftest-report :summary [m]
  (let [{:keys [pass fail error]} m
        style-fn #(style % :bright (cond (not (zero? error))
                                         :red
                                         (not (zero? fail))
                                         :yellow
                                         :else :green))]
    (ct/with-test-out
      (println (style-fn
                (str "\nRan " (:test m) " tests containing "
                     (+ (:pass m) (:fail m) (:error m)) " assertions.")))
      (println (style-fn (str (:fail m) " failures, "
                              (:error m) " errors."))))))

(defmethod difftest-report :begin-test-ns [m]
  (ct/with-test-out
    (println (style (str "\nTesting " (ns-name (:ns m))) :bright :underline))))

(defmethod difftest-report :end-test-ns [m])
(defmethod difftest-report :begin-test-var [m])
(defmethod difftest-report :end-test-var [m])

(defn run-tests
  "Same as clojure.test/run-tests but with modified reporting."
  ([] (run-tests *ns*))
  ([& namespaces]
     (binding [ct/report difftest-report]
       (apply ct/run-tests namespaces))))

