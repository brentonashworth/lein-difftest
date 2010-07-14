;; Copyright (c) Brenton Ashworth. All rights reserved.
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file COPYING at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.

(ns leiningen.test-difftest
  (:use (clojure test)))

(deftest test-something
  (testing "Are these two maps equal"
    (is (= {:a 1 :b 2 :c 3}
           {:a 1 :c 3 :d 1}))
    (is (= {:a 1 :b 2 :c 3}
           {:a 1 :c 3 :d 1}) "Another message")
    (is (= {:a 1 :b 2 :c 3}
           {:a 1 :b 2 :c 3}))))

(def large-map {:remote-addr "0:0:0:0:0:0:0:1%0"
                :scheme :http
                :query-params {}
                :session
                {:sandbar.stateful-session/session
                 {:project
                  {:host "localhost"
                   :name "deview/deview-server"
                   :port 9000
                   :_id 1
                   :_v 1
                   :_type :projects}}}
                :form-params {}
                :request-method :get
                :query-string nil
                :route-params {"*" "/deview/deview-server"}
                :content-type nil
                :cookies {"ring-session"
                          {:value "0bcca4e4-a852-4976-b2e9-a697a48f1ed6"}}
                :uri "/stats/deview/deview-server"
                :server-name "localhost"
                :params {"*" "/deview/deview-server"}})

(deftest test-large-map
  (is (= large-map
         (assoc-in large-map
                   [:cookies "ring-session" :value]
                   "0bcoa4e4-a852-4976-b2e9-a697a48f1ed6"))))

(deftest test-cause-exception
  (is (= {:a 1}
         (throw (Exception. "Hello")))))
