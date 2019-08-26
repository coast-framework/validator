## validator 2.0.0 (08/25/2019)

- Changed `params` to accept a ring request map and return either a nested map of the form `{:table {:column1 value1 :column2 value2}}`
- Also changed it to run `select-keys` on all `:required`, `:not-blank` and `:contains` validations
- Nested maps and qualified keyword maps both work now

This is a major change which makes it easier for things like this to happen:

```clojure
(ns your-ns
  (:require [coast]))

(def params
  (coast/params :table
    [:required [:column1 :column2]]))

(defn create [request]
  (let [{:keys [db]} request]
        [inserted-row errors] (->> (params request)
                                   (coast/insert db)
                                   (coast/rescue))
     (if (nil? errors)
       (coast/redirect-to :whatever)
       (build (assoc request :errors errors)))))
```

Which is much cleaner than the old `-> validate insert-cols` situation to try to select keys from the map after the fact.
