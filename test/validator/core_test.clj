(ns validator.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [validator.core :as validator]
            [error.core :as error]))


(deftest params-test
  (let [params (validator/params :account
                 [:required [:email :password]]
                 [:email [:email]])]

    (testing "with nil"
      (is (= [nil {:account {:email "Email must not be blank" :password "Password must not be blank"}}]
             (error/rescue
               (params nil)))))


    (testing "with an empty map"
      (is (= [nil {:account {:email "Email must not be blank" :password "Password must not be blank"}}]
             (error/rescue
              (params {})))))

    (testing "with an invalid nested map"
      (let [request {:params {:account {:email "" :password "pw"}}}]
        (is (= [nil {:account {:email "Email must not be blank"}}]
               (error/rescue
                (params request))))))

    (testing "with a valid map"
      (let [account {:account {:email "f@f.com" :password "pw"}}
            request {:params account}]
        (is (= account (params request)))))

    (testing "valid qualified keyword map"
      (let [account #:account{:email "f@f.com" :password "pw"}
            request {:params account}]
        (is (= account (params request)))))

    (testing "invalid qualified keyword map"
      (let [account #:account{:email "" :password "pw"}
            request {:params account}]
        (is (= [nil #:account{:email "Email must not be blank"}]
               (error/rescue
                (params request))))))

    (testing "does not throw when using :min-length"
      (let [account #:account{:email "f@f.com" :password "password123456"}
            request {:params account}
            params (validator/params :account
                     [:required [:email :password]]
                     [:min-length 10 :password])]
        (is (= [account nil]
               (error/rescue
                (params request))))))))
