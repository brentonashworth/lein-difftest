;; Copyright (c) Brenton Ashworth. All rights reserved.
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file COPYING at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.

(ns difftest.core
  (:require [clojure.test :as ct]
            [clojure.stacktrace :as stack]
            [com.georgejahad.difform :as difform]))

(defn difform-str [x y]
  (with-out-str
    (difform/clean-difform x y)))

(defmulti difftest-report :type)

(defmethod difftest-report :default [m]
  (ct/with-test-out (prn m)))

(defmethod difftest-report :pass [m]
  (ct/with-test-out (ct/inc-report-counter :pass)))

(defn actual-diff [form]
  (cond (and (= (first form) 'not) (= (first (last form)) '=))
        (let [[_ [_ actual expected]] form]
          (difform-str expected
                       actual))
        :else form))

(defmethod difftest-report :fail [m]
  (ct/with-test-out
   (ct/inc-report-counter :fail)
   (println "\nFAIL in" (ct/testing-vars-str))
   (when (seq ct/*testing-contexts*) (println (ct/testing-contexts-str)))
   (when-let [message (:message m)] (println message))
   (println "expected:" (pr-str (:expected m)))
   (println "  actual:\n" (let [actual (:actual m)]
                            (actual-diff actual)))))

(defmethod difftest-report :error [m]
  (ct/with-test-out
   (ct/inc-report-counter :error)
   (println "\nERROR in" (ct/testing-vars-str))
   (when (seq ct/*testing-contexts*) (println (ct/testing-contexts-str)))
   (when-let [message (:message m)] (println message))
   (println "expected:" (pr-str (:expected m)))
   (print " actual: ")
   (let [actual (:actual m)]
     (if (instance? Throwable actual)
       (stack/print-cause-trace actual ct/*stack-trace-depth*)
       (prn actual)))))

(defmethod difftest-report :summary [m]
  (ct/with-test-out
   (println "\nRan" (:test m) "tests containing"
            (+ (:pass m) (:fail m) (:error m)) "assertions.")
   (println (:fail m) "failures," (:error m) "errors.")))

(defmethod difftest-report :begin-test-ns [m]
  (ct/with-test-out
   (println "\nTesting" (ns-name (:ns m)))))

(defmethod difftest-report :end-test-ns [m])
(defmethod difftest-report :begin-test-var [m])
(defmethod difftest-report :end-test-var [m])

(defn run-tests
  ([] (run-tests *ns*))
  ([& namespaces]
     (binding [ct/report difftest-report]
       (apply ct/run-tests namespaces))))
