;; Copyright (c) Brenton Ashworth. All rights reserved.
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file COPYING at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.

(ns leiningen.hooks.difftest
  (:require leiningen.test)
  (:use robert.hooke))

(defn difftest-test-package-hook
  "Ensure that form-for-testing-namespaces always uses 'difftest.core as the
   test package."
  [f & args]
  `(do (require '~'difftest.core)
       ~(apply f args)))

(add-hook #'leiningen.test/form-for-testing-namespaces
          difftest-test-package-hook)
