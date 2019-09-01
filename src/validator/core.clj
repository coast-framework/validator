(ns validator.core
  (:require [jkkramer.verily :as v]
            [helper.core :as helper]
            [error.core :as error]
            [clojure.string :as string]))


(defn qualified-map? [m]
  (qualified-ident? (ffirst m)))


(defn nest [k v]
  (let [[table column] (->> (string/split (name k) #"\.")
                            (map keyword))]
    {table {column v}}))


(defn fmt-validation [result]
  (let [{:keys [keys msg]} result]
    (if (every? qualified-ident? keys)
      (mapv #(hash-map % (format "%s %s" (-> % name helper/humanize) msg)) keys)
      (mapv #(nest % (str (->> % name (re-find #"\.(\w+)") last helper/humanize) " " msg)) keys))))


(defn fmt-validations [results]
  (when (some? results)
    (->> (map fmt-validation results)
         (mapcat identity)
         (apply merge-with merge))))


(defn validate [m validations]
  (let [errors (-> (v/validate m validations)
                   (fmt-validations))]
    (if (empty? errors)
      m
      (error/raise errors))))


(defn nested-keyword [table field]
  (keyword (format "%s.%s" (name table) (name field))))


(defn validator-key? [keyword-k]
  (and (keyword? keyword-k)
    (some? (v/validations-map keyword-k))))


(defn format-rule-fields [table field-fn val]
  (cond
    (validator-key? val) val
    (and (sequential? val)
      (every? keyword? val)) (mapv #(field-fn (name table) (name %)) val)
    (keyword? val) (field-fn (name table) (name val))
    :else val))


(defn validation [table rule field-fn]
  (mapv #(format-rule-fields table field-fn %) rule))


(defn required-keys [validations]
  (->> (filter #(contains? #{:required :not-blank :contains} (first %)) validations)
       (mapcat second)
       (distinct)))


(defn params [table & validations]
  (fn [{:keys [params]}]
    (let [required-keys (required-keys validations)
          qualified-map? (qualified-map? params)
          field-fn (if qualified-map?
                     keyword
                     nested-keyword)
          validations (mapv #(validation table % field-fn) validations)
          validated-params (validate params validations)]
      (if qualified-map?
        (->> (map #(keyword (name table) (name %)) required-keys)
             (select-keys validated-params))
        {table (-> (get validated-params table)
                   (select-keys required-keys))}))))
