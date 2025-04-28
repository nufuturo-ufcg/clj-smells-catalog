# Catalog of Clojure-related code smells
This repository catalogs code smells in Clojure, providing descriptions, examples and causes.

# Table of Smells
* Traditional smells
  * Duplicated code
  * Long function
  * Long parameter list
  * Divergent change
  * Primitive obsession
  * Message chains
  * Middle man
  * Inappropriate intimacy
  * Comments
  * Mixed paradigms
  * Library locker
* Functional-related smells
  * Overuse of high-order functions
  * Trivial lambda
  * Deeply-nested call stacks
  * Inappropriate collection
  * Underutilizing clojure features
  * Premature optimization
  * Lazy side effects
  * Immutability violation
  * External data coupling
  * Inefficient filtering

# Introduction


# Traditional Smells

## Duplicated Code

* __Description:__ This smell occurs when identical or highly similar code appears in multiple locations within a codebase. It increases maintenance costs and the risk of inconsistencies, as a single change must be manually replicated across all instances. 

* __Example:__

```clojure
(defn info-log [message]
  (str "[INFO] " (clojure.string/upper-case message) " - " (java.time.Instant/now)))

(defn error-log [message]
  (str "[ERROR] " (clojure.string/upper-case message) " - " (java.time.Instant/now)))=
```

* __Refactoring:__ Instead of duplicating the log formatting logic for different log levels, it is better to refactor the code by centralizing the formatting responsibility in a single function. This refactoring improves maintainability by reducing duplicated code and making it easier to modify the log structure if needed.

```clojure
(defn format-log [level message]
  (str "[" level "] " (.toUpperCase message) " - " (java.time.Instant/now)))

(println (format-log "INFO" "Process started"))
(println (format-log "ERROR" "File not found"))
```

## Long Function

* __Description:__ This code smell occurs when a function grows excessively large, containing too many lines of code or performing too many operations. Long functions are harder to understand, maintain, and test. In functional programming, this smell is analogous to the "Long Method" smell commonly discussed in object-oriented paradigms.

* __Example:__

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

* __Description:__ This code smell occurs when a function or method accepts an excessive number of parameters. Long parameter lists make functions harder to understand, more error-prone to call, and more difficult to refactor.

* __Example:__

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

* __Description:__ This code smell occurs when a single module or function must be modified in multiple, unrelated ways for different reasons. It indicates poor separation of concerns and suggests that the code is handling responsibilities that should be split across different components.

* __Example:__ 

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

* __Description:__ This code smell occurs when the code relies too much on primitive types (such as integers, strings, or collections)  to represent an object in a domain, rather than defining richer, explicit types. Primitive obsession leads to weak domain modeling, reduces code readability, and makes validation and maintenance harder.

* __Example:__

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

* __Description:__ This code smell occurs when you chain method calls on top of other method calls, creating a long, fragile chain of calls. Each object in the chain exposes part of its internal structure to the caller, increasing coupling and making the code harder to read, maintain, and refactor. Changes to intermediate objects often require modifications throughout the chain, leading to brittle designs.

* __Example:__

* __Refactoring:__

## Middle Man

* __Description:__ This code smell occurs when a function serves mainly to pass data or delegate calls to another function without adding meaningful transformation, validation, or abstraction. This redundant indirection unnecessarily complicates the codebase and obscures the program's true flow. In object-oriented programming this smell often manifests through classes that merely forward method calls.

* __Example:__ 

``` clojure
(defn build-person [x]
  {:name (:name x) :age (:age x)})

(defn build-persons [xs]
  (map build-person xs))

(def people [{:name "Alice" :age 30}
             {:name "Bob" :age 25}])

(println (build-persons people))
```

* __Refactoring:__ The build-persons function only delegates to map without adding any behavior (Middle Man smell). Remove it and call map directly.

``` clojure
(defn build-person [x]
  {:name (:name x) :age (:age x)})

(def people [{:name "Alice" :age 30}
             {:name "Bob" :age 25}])

(println (map build-person people))
```

## Inappropriate Intimacy

* __Description:__ This code smell occurs when functions, modules, or data structures become overly dependent on each other's internal representations or behaviors. This tight coupling reduces modularity, complicates maintenance, and makes it harder to evolve or reason about the system independently.

* __Example:__ 

``` clojure
(def session
  (proxy [IDeref] []
    (deref [this]
      (read-session (.store this)))
    (store [this]
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

* __Description:__ This code smell occurs when functions, modules, or data structures become overly dependent on each other's internal representations or behaviors. This tight coupling reduces modularity, complicates maintenance, and makes it harder to evolve or reason about the system independently.

* __Example:__ 

``` clojure
(defn process-user [user]
  ;; validate input
  (when-not (:email user)
    (throw (Exception. "Missing email")))
  (when-not (:id user)
    (throw (Exception. "Missing ID")))

  ;; transform data
  (let [username (clojure.string/lower-case (:email user))
        uid (str "user-" (:id user))]

    ;; store in database
    (save-user {:username username
                :uid uid
                :email (:email user)})))
```

* __Refactoring:__ Comments are being used to separate logical sections of code. Instead, extract those sections into well-named functions. This improves readability and avoids the need for explanatory comments.

``` clojure
(defn validate-user [user]
  (when-not (:email user)
    (throw (Exception. "Missing email")))
  (when-not (:id user)
    (throw (Exception. "Missing ID")))
  user)

(defn transform-user [user]
  {:username (clojure.string/lower-case (:email user))
   :uid (str "user-" (:id user))
   :email (:email user)})

(defn process-user [user]
  (-> user
      validate-user
      transform-user
      save-user))
```

## Mixed Paradigms

* __Description:__ This code smell occurs when two or more distinct programming paradigms are used together in a way that forces interoperability. The blending of approaches — such as functional and imperative or object-oriented and procedural — can introduce confusion, reduce clarity, and complicate the design, as different paradigms impose conflicting constraints and expectations.

* __Example:__ 

``` clojure
(defrecord Counter [^:volatile-mutable ^long value])

(defn make-counter []
  (->Counter 0))

(defn increment-counter [^Counter c]
  (set! (.value c) (inc (.value c)))
  (.value c))

(def counter (make-counter))

(println (increment-counter counter)) ;; => 1
(println (increment-counter counter)) ;; => 2
```

* __Refactoring:__ This mixes object-oriented mutable state (set!, .value) into Clojure's functional paradigm, making the code imperative, thread-unsafe, and non-idiomatic. Prefer functional state tools like atom.

``` clojure
(defn make-counter []
  (atom 0))

(defn increment-counter [counter]
  (swap! counter inc))

(def counter (make-counter))

(println (increment-counter counter)) ;; => 1
(println (increment-counter counter)) ;; => 2
```

## Library Locker

* __Description:__ This code smell occurs when an application wraps a third-party library with its own functions or abstractions, often obscuring or complicating the library's usage.

* __Example:__

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

# Clojure-related Smells

## Overuse Of High-Order Functions

* __Description:__ This code smell occurs when nearly every function takes or returns another function, leading to excessive abstraction that makes the code unnecessarily complex and harder to follow. While higher-order functions enhance flexibility and reusability, their excessive use can obscure intent, making debugging and reasoning about the code more difficult.

* __Example:__

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

* __Description:__ This code smell occurs when anonymous functions (lambdas) are excessively used instead of named functions, reducing code clarity and reusability. This is especially problematic when lambda expressions are chained or become overly complex, making the code harder to read, understand, and maintain.

* __Example:__ 

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

* __Description:__ This code smell occurs when function calls are nested to a significant depth, resulting in a long chain of execution on the stack. This makes debugging more difficult, increases the risk of stack overflow, and reduces code readability by obscuring the control flow.

* __Example:__

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

* __Description:__ This code smell occurs when a data structure is used in a way that contradicts its intended purpose, leading to inefficient operations. For example, if a sequential collection is frequently scanned to locate elements by key, it likely indicates that an associative structure (e.g., a map or dictionary) would be more appropriate, improving both performance and clarity.

* __Example:__

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

* __Description:__ This code smell occurs when built-in language capabilities, such as higher-order functions, macros, or immutable data structures, are ignored in favor of more verbose or imperative approaches. This leads to less idiomatic, more error-prone, and harder-to-maintain code.

* __Example:__

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

* __Description:__ This code smell occurs when code is optimized too early, before verifying if performance is an actual bottleneck, leading to unnecessary complexity and reduced maintainability. For example, when `with-retry` is used to repeatedly attempt the same operation without assessing whether the real issue—such as incorrect credentials or permanent network failures—can be fixed, wasting resources instead of addressing the root cause.

* __Example:__

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

* __Description:__ This code smell occurs when side effects (such as state changes or external interactions) happen within lazy evaluated code. Since lazy evaluation delays computation until needed, these side effects may not execute as expected, leading to unpredictable behavior and making the code harder to debug and maintain. 

* __Example:__

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

* __Description:__ This code smell Immutability occurs when mutable state is used in a language or paradigm that emphasizes immutability (such as Clojure), leading to side effects, reduced predictability, and harder-to-maintain code. 

* __Example:__

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

* __Description:__ This code smell occurs when utilizing raw data from external sources as-is within your application, leading to tight coupling between your code and the external data structure. It is about how you model and transform external information.

* __Example:__

* __Refactoring:__


## Inefficient Filtering

* __Description:__ This code smell occurs when the data generator makes excessive use of filters (such as such-that) to constrain the generated values. Instead of genering only valid values directly, the generator creates a large number of values and filters them afterward, resulting in resource waste and potential performance issues.

* __Example:__

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
