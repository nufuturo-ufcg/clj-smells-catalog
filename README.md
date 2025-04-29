# clojure-code-smells-catalog
This repository catalogs code smells in Clojure, providing descriptions, examples and causes.

## Table of Contents


## Introduction


## Duplicated Code

```clojure
(defn info-log [message]
  (str "[INFO] " (clojure.string/upper-case message) " - " (java.time.Instant/now)))

(defn error-log [message]
  (str "[ERROR] " (clojure.string/upper-case message) " - " (java.time.Instant/now)))

(println (info-log "Process started"))
(println (error-log "File not found"))
```

* __Refactoring:__ When multiple functions perform nearly identical operations with only slight variations (like a fixed log level), it's better to unify the shared logic into a single, parameterized function. This reduces repetition, improves clarity, and makes future changes easier to implement.

```clojure
(defn format-log [level message]
  (str "[" level "] " (.toUpperCase message) " - " (java.time.Instant/now)))

(println (format-log "INFO" "Process started"))
(println (format-log "ERROR" "File not found"))
```

## Long Function

```clojure
(defn process-users [users]
  (let [filtered (filter #(> (:age %) 18) users)
        formatted (map #(str (:first-name %) " " (:last-name %)) filtered)
        report (map #(hash-map :full-name %1 :age (:age %2)) formatted filtered)]
    report))

(println (process-users [{:first-name "Alice" :last-name "Smith" :age 22}
                         {:first-name "Bob" :last-name "Johnson" :age 17}
                         {:first-name "Charlie" :last-name "Brown" :age 25}])) 
```

* __Refactoring:__ Long function make the code harder to understand and reuse. By breaking them into smaller, well-named functions, we improve modularity and maintainability.

```clojure
(defn is-adult? [user]
  (> (:age user) 18))

(defn format-user [user]
  {:full-name (str (:first-name user) " " (:last-name user))
   :age (:age user)})

(defn process-users [users]
  (map format-user (filter is-adult? users)))

(println (process-users [{:first-name "Alice" :last-name "Smith" :age 22}
                         {:first-name "Bob" :last-name "Johnson" :age 17}
                         {:first-name "Charlie" :last-name "Brown" :age 25}])) 
```

## Long Parameter List

```clojure
(defn create-user [first-name last-name age email address phone]
  {:first-name first-name
   :last-name last-name
   :age age
   :email email
   :address address
   :phone phone})

(println (create-user "Alice" "Smith" 30 "alice@example.com" "123 Main St" "555-1234"))
```

* __Refactoring:__ A long parameter list can be simplified by grouping related data into a single map, making the function easier to use and extend.

```clojure
(defn create-user [user-info]
  {:first-name (:first-name user-info)
   :last-name (:last-name user-info)
   :age (:age user-info)
   :email (:email user-info)
   :address (:address user-info)
   :phone (:phone user-info)})

(println (create-user {:first-name "Alice" 
                       :last-name "Smith" 
                       :age 30 
                       :email "alice@example.com" 
                       :address "123 Main St" 
                       :phone "555-1234"}))
```

## Divergent Change

```clojure
(defn process-user [user]
  (let [full-name (str (:first-name user) " " (:last-name user))
        valid-age? (>= (:age user) 18)]
    (if valid-age?
      (do
        (println (str "User " full-name " is valid. Sending notification..."))
        {:full-name full-name :status "Valid"})
      (do
        (println (str "User " full-name " is not valid. No notification sent."))
        {:full-name full-name :status "Invalid"}))))

(println (process-user {:first-name "Alice" :last-name "Smith" :age 22}))
```

* __Refactoring:__ To reduce Divergent Change, we separate concerns into different functions. Now, formatting, validation, and notifications are handled independently, making modifications safer and more maintainable.

```clojure
(defn format-name [user]
  (str (:first-name user) " " (:last-name user)))

(defn valid-age? [user]
  (>= (:age user) 18))

(defn send-notification [user]
  (println (str "User " (:full-name user) " is valid. Sending notification...")))

(defn process-user [user]
  (let [full-name (format-name user)
        user-data {:full-name full-name :status (if (valid-age? user) "Valid" "Invalid")}]
    (when (= "Valid" (:status user-data))
      (send-notification user-data))
    user-data))

(println (process-user {:first-name "Alice" :last-name "Smith" :age 22}))
```

## Primitive Obsession

``` clojure
(defn format-price [amount currency]
  (format "%.2f %s" amount currency))

(println (format-price 9.99 "USD"))
```

* __Refactoring:__ Passing raw primitives scatters formatting and rules; wrap amount and currency in a Money record. 

``` clojure
(defrecord Money [amount currency])

(defn format-price [^Money m]
  (format "%.2f %s" (:amount m) (:currency m)))

(println (format-price (->Money 9.99 "USD")))
```

## Message Chains

## Middle Man

``` clojure
(defn build-person [x]
  {:name (:name x) :age (:age x)})

(defn build-persons [xs]
  (map build-person xs))

(def people [{:name "Alice" :age 30}
             {:name "Bob" :age 25}])

(println (build-persons people))
```

* __Refactoring:__ When a function only serves as a pass-through to another function (e.g., wrapping map, filter, or similar without adding any new behavior), it introduces unnecessary indirection. It's better to remove the intermediary and use the higher-order function directly. This simplifies the code, makes dependencies more explicit, and improves maintainability.

``` clojure
(defn build-person [x]
  {:name (:name x) :age (:age x)})

(def people [{:name "Alice" :age 30}
             {:name "Bob" :age 25}])

(println (map build-person people))
```

## Inappropriate Intimacy

``` clojure
(def session
  (proxy [clojure.lang.IDeref] []
    (deref []
      {:user-id 42 :role "admin"})
    (store []
      {:type ::memory-store})))

(println @session)
```

* __Refactoring:__ The session object mixes concerns: it implements a core protocol (IDeref) but leaks storage-specific logic (e.g. read-session, .store). This creates tight coupling and misuse of IDeref. Replace with a plain function and protocol for clarity and separation.

``` clojure
(defprotocol SessionStore
  (read-session [this]))

(defrecord MemoryStore []
  SessionStore
  (read-session [_]
    {:user-id 42 :role "admin"}))

(defn get-session [store]
  (read-session store))

(def session-store (->MemoryStore))

(println (get-session session-store))
```

## Comments

``` clojure
(ns examples.smells.comments
  (:require [clojure.string :as str]))

(defn save-user [user]
  ;; simulate saving to database
  (println "Saving user:" user))

(defn process-user [user]
  ;; validate input
  (when-not (:email user)
    (throw (Exception. "Missing email")))
  (when-not (:id user)
    (throw (Exception. "Missing ID")))

  ;; transform data
  (let [username (str/lower-case (:email user))
        uid (str "user-" (:id user))]

    ;; store in database
    (save-user {:username username
                :uid uid
                :email (:email user)})))

(process-user {:id 1 :email "Exemplo@Email.com"})
```

* __Refactoring:__ Comments are being used to separate logical sections of code. Instead, extract those sections into well-named functions. This improves readability and avoids the need for explanatory comments.

``` clojure
(ns examples.smells.comments
  (:require [clojure.string :as str]))

(defn save-user [user]
  (println "Saving user:" user))

(defn validate-user [user]
  (when-not (:email user)
    (throw (Exception. "Missing email")))
  (when-not (:id user)
    (throw (Exception. "Missing ID")))
  user)

(defn transform-user [user]
  {:username (str/lower-case (:email user))
   :uid (str "user-" (:id user))
   :email (:email user)})

(defn process-user [user]
  (-> user
      validate-user
      transform-user
      save-user))

(process-user {:id 1 :email "Exemplo@Email.com"})
```

## Mixed Paradigms

``` clojure
(defrecord Counter [value])

(defn make-counter []
  (->Counter (atom 0)))

(defn increment-counter [^Counter c]
  (swap! (:value c) inc))

(def counter (make-counter))

(println (increment-counter counter))
(println (increment-counter counter))
```

* __Refactoring:__ This mixes object-oriented mutable state (set!, .value) into Clojure's functional paradigm, making the code imperative, thread-unsafe, and non-idiomatic. Prefer functional state tools like atom.

``` clojure
(defn make-counter []
  (atom 0))

(defn increment-counter [counter]
  (swap! counter inc))

(def counter (make-counter))

(println (increment-counter counter))
(println (increment-counter counter))
```

## Library Locker

``` clojure
(ns app.http
  (:require [app.auth-tokens :as auth]
            [clj-http.client :as http]
            [clojure.tools.logging :as log]
            [jsonista :as j]))

(defn get
  "Custom wrapper around clj-http/client `get` with logging, tracing, auth, and JSON parsing."
  [url
   {:keys [query-params] :as http-options}
   {user-id    :user/id
    star-id    :star/id
    root-trace :root-trace/id
    request-id :request/id
    service    :service/name
    :keys      [originating-system]
    :or        {originating-system "observatory"}
    :as options}]
  (log/infof "User %s is requesting star %s. Request id: %s. Trace id %s"
             user-id request-id root-trace)
  (let [response (http/get url
                           (-> http-options
                               (assoc :oauth (get auth/tokens service))
                               (assoc-in [:headers :user-id] user-id)
                               (assoc-in [:headers :request-id] request-id)
                               (assoc-in [:headers :root-trace] root-trace)
                               (assoc-in [:headers :originating-system] originating-system)))
        body (j/read-value (:body response))]
    (log/infof "Received star: %s. Request id: %s. Trace id %s"
               body request-id root-trace)
    body))
```

* __Refactoring:__ The get function wraps a third-party library (clj-http) and mixes multiple concerns (logging, auth, tracing, parsing). This tightly couples the app to that library and makes future changes harder. Use function composition to separate and reuse behaviors.

``` clojure
(defn inject-auth [service req]
  (assoc-in req [:headers :oauth] (get auth/tokens service)))

(defn inject-user-id [user-id req]
  (assoc-in req [:headers :user-id] user-id))

(defn inject-trace-id [trace-id req]
  (assoc-in req [:headers :root-trace] trace-id))

(defn inject-origin [origin req]
  (assoc-in req [:headers :originating-system] origin))

(defn prepare-request
  [{:keys [user/id root-trace/id service/name originating-system]
    :or   {originating-system "observatory"}}]
  (comp
    (partial inject-auth service/name)
    (partial inject-user-id user/id)
    (partial inject-trace-id root-trace/id)
    (partial inject-origin originating-system)))

(defn fetch
  "Generic HTTP fetch with optional transformations and logging"
  [url http-options context]
  (let [req-builder (prepare-request context)
        full-options (req-builder http-options)
        response (http/get url full-options)]
    (j/read-value (:body response))))

;; Em `app.service.night-sky`
(defn find-star
  [{:user/keys [id] :star/keys [id] :root-trace/keys [id]} :as ctx]
  (fetch "http://host.night-sky/api/star"
         {:query-params {:star-id id}}
         (assoc ctx :service/name :night-sky)))
```







## Overuse Of High-Order Functions

``` clojure
(defn apply-twice [f x]
  (f (f x)))

(defn transform-list [f coll]
  (map #(apply-twice f %) coll))

(defn process-data [data]
  (let [double (fn [x] (* 2 x))]
    (transform-list double data)))

(println (process-data [1 2 3 4])) 
```

* __Refactoring:__ Excessive use of higher-order functions adds unnecessary complexity and makes the code harder to understand. Instead of layering multiple functions, we can simplify the logic by directly applying the required transformation.

```clojure
(defn process-data [data]
  (map #(* 4 %) data))

(println (process-data [1 2 3 4])) 
```

## Trivial Lambda

``` clojure
(defn square [x]
  (* x x))

(def numbers [1 2 3 4])

(println (map #(square %) numbers))
```

* __Refactoring:__ The anonymous function #(square %) does nothing more than calling square directly. This is redundant. Pass the function by name instead.

``` clojure
(defn square [x]
  (* x x))

(def numbers [1 2 3 4])

(println (map square numbers))
```

## Deeply-nested Call Stacks

``` clojure
(defn sanitize [s]
  (clojure.string/trim (clojure.string/lower-case (clojure.string/replace s #"[^a-zA-Z0-9]" ""))))

(defn process-user [user]
  (assoc user :username (sanitize (:name (first (sort-by :created-at (:accounts user)))))))

(def users [{:name "Alice"
             :accounts [{:created-at "2020-01-01" :name "Main"}]}
            {:name "Bob"
             :accounts [{:created-at "2019-05-01" :name "Legacy"}]}])

(println (map process-user users))
```

* __Refactoring:__ Deeply nested expressions (especially within sanitize) reduce readability and make debugging harder. Break the nested calls into intermediate steps.

``` clojure
(defn sanitize [s]
  (-> s
      (clojure.string/replace #"[^a-zA-Z0-9]" "")
      clojure.string/lower-case
      clojure.string/trim))

(defn earliest-account-name [accounts]
  (:name (first (sort-by :created-at accounts))))

(defn process-user [user]
  (let [account-name (earliest-account-name (:accounts user))
        username (sanitize account-name)]
    (assoc user :username username)))

(def users [{:name "Alice"
             :accounts [{:created-at "2020-01-01" :name "Main"}]}
            {:name "Bob"
             :accounts [{:created-at "2019-05-01" :name "Legacy"}]}])

(println (map process-user users))
```

## Inappropriate Collection

``` clojure
(def people
  [{:person/name "Fred"}
   {:person/name "Ethel"}
   {:person/name "Lucy"}])

(defn person-in-people?
  [person people]
  (some #(= person (:person/name %)) people))

(person-in-people? "Fred" people) ;; => true
```

* __Refactoring:__ Scanning a sequential collection to find an item by a key is inefficient and obscures intent. Use an associative structure like group-by to enable fast, direct access.

``` clojure
(def collected-people
  (group-by :person/name people))

(contains? collected-people "Fred") ;; => true
```

## Underutilizing Clojure Features

``` clojure
(defn duplicate-and-wrap [x]
  [(str "<" x ">") (str "<" x ">")])

(def values ["a" "b" "c"])

(println (apply concat (map duplicate-and-wrap values)))
;; => ("<a>" "<a>" "<b>" "<b>" "<c>" "<c>")
```

* __Refactoring:__ Using apply concat (map ...) is functionally correct but unnecessarily verbose. Clojure provides mapcat to express this pattern more idiomatically and efficiently.

``` clojure
(defn duplicate-and-wrap [x]
  [(str "<" x ">") (str "<" x ">")])

(def values ["a" "b" "c"])

(println (mapcat duplicate-and-wrap values))
;; => ("<a>" "<a>" "<b>" "<b>" "<c>" "<c>")
```

## Premature Optimization

``` clojure
(defmacro with-retry [[attempts timeout] & body]
  `(loop [n# ~attempts]
     (let [[e# result#]
           (try
             [nil (do ~@body)]
             (catch Throwable e#
               [e# nil]))]
       (cond
         (nil? e#) result#
         (> n# 0) (do (Thread/sleep ~timeout)
                     (recur (dec n#)))
         :else (throw (new Exception "all attempts exhausted" e#))))))

(with-retry [3 2000]
  (get-file-from-network "/path/to/file.txt"))
```

* __Refactoring:__ This macro retries blindly on failure, regardless of the error type. It introduces complexity early, without knowing if retrying is the right solution. Prefer explicit handling with functions and selective retry logic.

``` clojure
(defn with-retry [[attempts timeout] func]
  (loop [n attempts]
    (let [[e result]
          (try
            [nil (func)]
            (catch Throwable e
              [e nil]))]
      (cond
        (nil? e) result
        (> n 0) (do (Thread/sleep timeout)
                    (recur (dec n)))
        :else (throw (Exception. "all attempts exhausted" e))))))

(with-retry [3 2000]
  #(get-file-from-network "/path/to/file.txt"))
```

## Lazy Side Effects

``` clojure
(defn notify [x]
  (println "Notifying value:" x)
  x)

(def data (range 3))

(->> data
     (map notify)
     (filter even?))
;; No output printed
```

* __Refactoring:__ Lazy sequences defer execution, which causes side effects like println to never run unless explicitly realized. Use into with transducers to make evaluation eager and effects reliable.

``` clojure
(defn notify [x]
  (println "Notifying value:" x)
  x)

(def data (range 3))

(into []
      (comp
        (map notify)
        (filter even?))
      data)
;; Prints:
;; Notifying value: 0
;; Notifying value: 1
;; Notifying value: 2
```

## Immutability Violation

``` clojure
(def countries {})

(defn update-country [country]
  (def countries (assoc countries (:name country) country)))

(update-country {:name "Brazil" :pop 210})
(println countries)
```

* __Refactoring:__ Avoid redefining vars inside functions. Make your updater return a new value so state stays immutable.

``` clojure
(defn update-country [countries country]
  (assoc countries (:name country) country))

(let [countries (update-country {} {:name "Brazil" :pop 210})]
  (println countries))
```

## External Data Coupling

## Inefficient Filtering

``` clojure
(require '[clojure.test.check.generators :as gen])

(def gen-even-int
  (gen/such-that even? gen/int))

(println (gen/sample gen-even-int 5))
```

* __Refactoring:__ Replace broad, post-filtered generators with ones that produce only valid values, eliminating wasted generation.

``` clojure
(require '[clojure.test.check.generators :as gen])

(def gen-even-int
  (gen/fmap #(* 2 %) (gen/choose 0 500)))

(println (gen/sample gen-even-int 5))
```
