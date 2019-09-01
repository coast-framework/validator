# validator
Easy clojure form validations

## Installation

Make your `deps.edn` look like this:

```clojure
coast-framework/validator {:mvn/version "2.0.1"}
```

## Usage

Require it like this

```clojure
(ns your-project
  (:require [validator.core :as validator]))
```

First, define the table and columns to be validated

```clojure
(def params
  (validator/params :account
    [:required [:email :password]]))
```

Then validate a ring request map

```clojure
(let [account {:params {:account {:email "f@f.com" :password "correct battery horse staple"}}}]
  (params account))

; returns
{:account {:email "f@f.com" :password "correct battery horse staple"}}
```

Qualified keyword maps work too!

```clojure
(params {:params {:account/email "f@f.com"}})

; => returns {:account/email "f@f.com"}
```

When there's a validation error, an exception is thrown

```clojure
(params {:params {:account/email ""}})

; => clojure.lang.ExceptionInfo {:ex-data :error.core/e {:email "Email must not be blank"}}
```

Custom messages are supported

```clojure
(def params
  (validator/params :account
    [:required [:email :password] "is required"]))

(params {:params {}})

; => clojure.lang.ExceptionInfo {:ex-data :error.core/e {:email "Email is required" :password "Password is required"}}
```

### Built in validators

Below is the list of available, built in validator rules

- :required <keys> [msg] - must not be absent, blank, or nil
- :contains <keys> [msg] - must not be absent, but can be blank or nil
- :not-blank <keys> [msg] - may be absent but not blank or nil
- :exact <value> <keys> [msg] - must be a particular value
- :equal <keys> [msg] - all keys must be equal
- :email <keys> [msg] - must be a valid email
- :url <keys> [msg] - must be a valid URL
- :web-url <keys> [msg] - must be a valid website URL (http or https)
- :link-url <keys> [msg] - must be a valid link URL (can be relative, http: or https: may be omitted)
- :matches <regex> <keys> [msg] - must match a regular expression
- :min-length <len> <keys> [msg] - must be a certain length (for strings or collections)
- :max-length <len> <keys> [msg] - must not exceed a certain length (for strings or collections)
- :complete <keys> [msg] - must be a collection with no blank or nil values
- :min-val <min> <keys> [msg] - must be at least a certain value
- :max-val <max> <keys> [msg] - must be at most a certain value
- :within <min> <max> <keys> [msg] - must be within a certain range (inclusive)
- :positive <keys> [msg] - must be a positive number
- :negative <keys> [msg] - must be a negative number
- :after <date> <keys> [msg] - must be after a certain date
- :before <date> <keys> [msg] - must be before a certain date
- :in <coll> <keys> [msg] - must be contained within a collection
- :every-in <coll> <keys> [msg] - each value must be within a collection (for values that are themselves collections)
- :us-zip <keys> [msg] - must be a valid US zip code
- :luhn <keys> [msg] - must be pass the Luhn check (e.g., for credit card numbers)
- Datatype validations: :string, :boolean, :integer, :float, :decimal, :date (plus aliases)
- Datatype collection validations: :strings, :booleans, :integers, :floats, :decimals, :dates (plus aliases)

This library uses [verily](https://github.com/jkk/verily) under the hood.

## Testing

```sh
cd validator && make test
```

## License

MIT

## Contribution

Create an issue, star it, make a pull request, there are no rules. Anarchy contribution.
