;; Copyright (c) Brenton Ashworth. All rights reserved.
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file COPYING at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.

(ns leiningen.difftest
  (:refer-clojure :exclude [test])
  (:use leiningen.test
        [clojure.java.io :only [file]]
        [clojure.contrib.find-namespaces :only [find-namespaces-in-dir]]
        [leiningen.compile :only [eval-in-project]]))

;; Don't mind if do use that priviate function.

(def with-version-guard 
     (ns-resolve 'leiningen.test
                 'with-version-guard))

;; The difftest function is the same as leiningen.test/test but calls
;; form-for-testing-namespaces with difftest.core as the
;; test-package.

(defn difftest
  "Same as 'lein test' but with better error reporting; shows a diff on
   test failures and uses clj-stacktrace for better stacktraces."
  [project & namespaces]
  (let [namespaces (if (empty? namespaces)
                     (sort (find-namespaces-in-dir (file (:test-path project))))
                     (map symbol namespaces))
        result (java.io.File/createTempFile "lein" "result")]
    (eval-in-project project
                     (with-version-guard
                       (form-for-testing-namespaces namespaces
                                                    'difftest.core
                                                    (.getAbsolutePath result))))
    (if (and (.exists result) (pos? (.length result)))
      (let [summary (read-string (slurp (.getAbsolutePath result)))
            success? (zero? (+ (:error summary) (:fail summary)))]
        (.delete result)
        (if success? 0 1))
      1)))

