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

    (testing "with an invalid map"
      (let [account {:account {:email "" :password "pw"}}]
        (is (= [nil {:account {:email "Email must not be blank"}}]
               (error/rescue
                (params account))))))

    (testing "with a valid map"
      (let [account {:account {:email "f@f.com" :password "pw"}}]
        (is (= account (params account)))))))
