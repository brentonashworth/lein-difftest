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

(def ^{:dynamic true} *color* true)

(defn difform-str
  "Create a string that is the diff of the forms x and y."
  [x y]
  (subs
   (with-out-str
     (difform/clean-difform x y)) 1))

(defmulti diff? (fn [form] (when (coll? form) (first form))))

(defmethod diff? :default [form]
  false)

(defmethod diff? 'not [form]
  (diff? (last form)))

(defmethod diff? '= [form]
  (let [a (second form)
        b (last form)]
    (or (and (coll? a) (coll? b))
        (and (string? a) (string? b)))))

(defn actual-diff
  "Transform the actual form that comes from clojure.test into a diff
   string. This will diff forms like (not (= ...)) and will return the string
   representation of anything else."
  [form]
  (if (diff? form)
    (let [[_ [_ actual expected]] form]
      (difform-str expected
                   actual))
    form))

(defn get-testing-vars-str
  "Wrap clojure.test/testing-vars-str to make it compatible with Clojure 1.1
   and 1.2."
  [m]
  (try (ct/testing-vars-str)
       (catch Exception _ (ct/testing-vars-str m))))

(defn print-with-style [styles & args]
  (if *color*
    (println (apply style (apply str (interpose " " args)) styles))
    (apply println args)))

(defn run-tests
  ([] (run-tests *ns*))
  ([& namespaces]
     (apply ct/run-tests namespaces)))

(defn test-ns
  ([] (test-ns *ns*))
  ([& namespaces]
     (let [namespaces (map symbol namespaces)]
       (doseq [n namespaces]
         (require :reload-all n))
       (binding [*color* false]
         (apply run-tests namespaces)))))

(defn activate
  "Redefine clojure.test/report methods to be difftest-aware."
  []
  (defmethod ct/report :fail [m]
    (ct/with-test-out
      (ct/inc-report-counter :fail)
      (print-with-style [:bright] "\nFAIL in" (get-testing-vars-str m))
      (when (seq ct/*testing-contexts*)
        (print-with-style [:bright] (ct/testing-contexts-str)))
      (when-let [message (:message m)] (print-with-style [:bright] message))
      (println "expected:" (pr-str (:expected m)))
      (println "  actual:\n" (let [actual (:actual m)]
                               (actual-diff actual)))))

  (defmethod ct/report :error [m]
    (ct/with-test-out
      (ct/inc-report-counter :error)
      (print-with-style [:bright :red] "\nERROR in" (get-testing-vars-str m))
      (when (seq ct/*testing-contexts*) (println (ct/testing-contexts-str)))
      (when-let [message (:message m)] (println message))
      (println "expected:" (pr-str (:expected m)))
      (print " actual: ")
      (let [actual (:actual m)]
        (if (instance? Throwable actual)
          (println (clj-stacktrace.repl/pst-str actual))
          (prn actual)))))

  (defmethod ct/report :summary [m]
    (let [{:keys [pass fail error]} m
          color (cond (not (zero? error))
                      :red
                      (not (zero? fail))
                      :yellow
                      :else :green)]
      (ct/with-test-out
        (print-with-style [:bright color]
          "\nRan" (:test m) "tests containing"
          (+ (:pass m) (:fail m) (:error m)) "assertions.")
        (print-with-style [:bright color]
          (:fail m) "failures," (:error m) "errors."))))

  (defmethod ct/report :begin-test-ns [m]
    (ct/with-test-out
      (print-with-style [:bright :underline] "\nTesting" (ns-name (:ns m))))))