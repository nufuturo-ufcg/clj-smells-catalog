# Catalog of Clojure-related code smells

This repository presents a catalog of code smells relevant to the Clojure ecosystem. The methodology follows the [original study](https://doi.org/10.1007/s10664-023-10343-6) on Elixir by Vegi & Valente (2023), with adaptations for the specifics of Clojure.

In a nutshell, we analyzed developer discussions from forums, blogs, and other practitioner sources to identify recurring problematic patterns that experienced Clojure developers frequently encounter and discuss. The catalog was compiled from real-world examples shared by practitioners across various online platforms.

The identified smells were organized in this repository, each with a description, code example, refactoring suggestion, and refactored version.

This catalog reflects the current stage of our ongoing study. We plan to expand it in future phases, following the model of the Elixir work. Contributions are welcome via issues and pull requests.

# Table of Smells
* [Traditional smells](#traditional-smells)
  * [Duplicated code](#duplicated-code)
  * [Long function](#long-function)
  * [Long parameter list](#long-parameter-list)
  * [Divergent change](#divergent-change)
  * [Shotgun surgery](#shotgun-surgery)
  * [Primitive obsession](#primitive-obsession)
  * [Message chains](#message-chains)
  * [Middle man](#middle-man)
  * [Inappropriate intimacy](#inappropriate-intimacy)
  * [Comments](#comments)
  * [Mixed paradigms](#mixed-paradigms)
  * [Library locker](#library-locker)
* [Functional-related smells](#functional-related-smells)
  * [Overabstracted composition](#overabstracted-composition)
  * [Overuse of high-order functions](#overuse-of-high-order-functions)
  * [Trivial lambda](#trivial-lambda)
  * [Deeply-nested call stacks](#deeply-nested-call-stacks)
  * [Inappropriate collection](#inappropriate-collection)
  * [Premature optimization](#premature-optimization)
  * [Lazy side effects](#lazy-side-effects)
  * [External data coupling](#external-data-coupling)
  * [Inefficient filtering](#inefficient-filtering)
  * [Hidden side effects](#hidden-side-effects)
  * [Explicit recursion](#explicit-recursion)
  * [Reinventing the wheel](#reinventing-the-wheel)
  * [Positional return values](#positional-return-values)
* [Clojure-specific smells](#clojure-specific-smells)
  * [Unnecessary macros](#unnecessary-macros)
  * [Immutability violation](#lazy-side-effects)
  * [Namespaced keys neglect](#namespaced-keys-neglect)
  * [Improper emptiness check](#improper-emptiness-check)
  * [Accessing non-existent map fields](#accessing-non-existent-map-fiels)
  * [Unnecessary `into`](#unnecessary-into)
  * [Conditional build-up](#conditional-build-up)
  * [Verbose checks](#verbose-checks)
  * [Production `doall`](#production-doall)
  * [Redundant `do` block](#redundant-do-block)
  * [Thread ignorance](#thread-ignorance)
  * [Nested forms](#nested-forms)
  * [Direct use of `clojure.lang.RT`](#direct-usage-of-clojurelangrt)

# Traditional Smells

## Duplicated Code

* __Description:__ This smell occurs when identical or highly similar code appears in multiple locations within a codebase. It increases maintenance costs and the risk of inconsistencies, as a single change must be manually replicated across all instances. 

* __Example:__

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

* __Description:__ This code smell occurs when a function grows excessively large, containing too many lines of code or performing too many operations. Long functions are harder to understand, maintain, and test. In functional programming, this smell is analogous to the "Long Method" smell commonly discussed in object-oriented paradigms.

* __Example:__

```clojure
(defn validate-user
  [user]
  (let [errors (atom [])]

    (if (string? (:name user))
      (do
        (when (< (count (:name user)) 2)
          (swap! errors conj "Name is too short."))
        (when (> (count (:name user)) 100)
          (swap! errors conj "Name is too long."))
        (when (str/blank? (:name user))
          (swap! errors conj "Name cannot be blank."))
        (when (re-find #"\d" (:name user))
          (swap! errors conj "Name must not contain numbers.")))
      (swap! errors conj "Invalid name."))

    (let [email (:email user)]
      (if (string? email)
        (do
          (if (not (re-matches #".+@.+\..+" email))
            (swap! errors conj "Invalid email format."))
          (if (or (str/includes? email "spam")
                  (str/includes? email "fake"))
            (swap! errors conj "Email contains suspicious terms."))
          (when (str/ends-with? email ".xyz")
            (swap! errors conj "Emails ending in .xyz are not allowed.")))
        (swap! errors conj "Email must be a string.")))

    (let [pwd (:password user)]
      (if (string? pwd)
        (do
          (when (not-any? #(Character/isUpperCase %) pwd)
            (swap! errors conj "Password must contain an uppercase letter."))
          (when (not-any? #(Character/isDigit %) pwd)
            (swap! errors conj "Password must contain a number."))
          (when (not-any? #(contains? #{\! \@ \# \$ \% \&} %) pwd)
            (swap! errors conj "Password must contain a special character."))
          (when (str/includes? pwd " ")
            (swap! errors conj "Password must not contain spaces.")))
        (swap! errors conj "Invalid password.")))

    (let [age (:age user)]
      (cond
        (nil? age) (swap! errors conj "Age is required.")
        (not (number? age)) (swap! errors conj "Age must be a number.")
        (> age 120) (swap! errors conj "Invalid age.")))

    (let [prefs (:preferences user)]
      (if (sequential? prefs)
        (do
          (when (empty? prefs)
            (swap! errors conj "Preferences list is empty."))
          (when (> (count prefs) 20)
            (swap! errors conj "Too many preferences.")))
        (swap! errors conj "Preferences must be a list.")))

    (if (empty? @errors)
      {:valid true}
      {:valid false :errors @errors}))) 
```

* __Refactoring:__ Long function make the code harder to understand and reuse. By breaking them into smaller, well-named functions, we improve modularity and maintainability.

```clojure
(defn validate-name [name errors]
  (if (string? name)
    (do
      (when (< (count name) 2)
        (swap! errors conj "Name is too short."))
      (when (> (count name) 100)
        (swap! errors conj "Name is too long."))
      (when (str/blank? name)
        (swap! errors conj "Name cannot be blank."))
      (when (re-find #"\d" name)
        (swap! errors conj "Name must not contain numbers.")))
    (swap! errors conj "Invalid name.")))

(defn validate-email [email errors]
  (if (string? email)
    (do
      (when (not (re-matches #".+@.+\..+" email))
        (swap! errors conj "Invalid email format."))
      (when (or (str/includes? email "spam")
                (str/includes? email "fake"))
        (swap! errors conj "Email contains suspicious terms."))
      (when (str/ends-with? email ".xyz")
        (swap! errors conj "Emails ending in .xyz are not allowed.")))
    (swap! errors conj "Email must be a string.")))

(defn validate-password [pwd errors]
  (if (string? pwd)
    (do
      (when (not-any? #(Character/isUpperCase %) pwd)
        (swap! errors conj "Password must contain an uppercase letter."))
      (when (not-any? #(Character/isDigit %) pwd)
        (swap! errors conj "Password must contain a number."))
      (when (not-any? #(contains? #{\! \@ \# \$ \% \&} %) pwd)
        (swap! errors conj "Password must contain a special character."))
      (when (str/includes? pwd " ")
        (swap! errors conj "Password must not contain spaces.")))
    (swap! errors conj "Invalid password.")))

(defn validate-age [age errors]
  (cond
    (nil? age) (swap! errors conj "Age is required.")
    (not (number? age)) (swap! errors conj "Age must be a number.")
    (> age 120) (swap! errors conj "Invalid age.")))

(defn validate-preferences [prefs errors]
  (if (sequential? prefs)
    (do
      (when (empty? prefs)
        (swap! errors conj "Preferences list is empty."))
      (when (> (count prefs) 20)
        (swap! errors conj "Too many preferences.")))
    (swap! errors conj "Preferences must be a list.")))

(defn validate-user [user]
  (let [errors (atom [])]
    (validate-name (:name user) errors)
    (validate-email (:email user) errors)
    (validate-password (:password user) errors)
    (validate-age (:age user) errors)
    (validate-preferences (:preferences user) errors)

    (if (empty? @errors)
      {:valid true}
      {:valid false :errors @errors}))) 
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

## Shotgun Surgery

* __Description:__ This code smell occurs when a single type of change requires numerous small edits across many different modules, functions, or files. When modifications are spread throughout the codebase, it becomes difficult to identify all the affected areas, increasing the risk of introducing inconsistencies or missing critical updates.

* __Example:__ 
``` clojure
(defn save-user [user]
  (println "Saving to DB:" (:name user) (:email user) (:age user)))

(defn send-welcome-email [user]
  (println "Sending email to:" (:email user)))

(defn track-user [user]
  (println "Tracking new user:" (:name user) (:age user)))

(defn create-user [name email age]
  (let [user {:name name
              :email email
              :age age}]
    (save-user user)
    (send-welcome-email user)
    (track-user user)))
    
(comment
  (create-user "Alice" "alice@example.com" 30))
```

* __Refactoring:__ To refactor the smell, centralize the logic or data structure that is being repeatedly modified across the codebase. Encapsulate related operations—like construction, access, or transformation—into a single module or set of functions. Update all scattered usages to rely on this central abstraction.

``` clojure
(defn make-user [{:keys [name email age]}]
  {:name name
   :email email
   :age age})

(defn get-name [user] (:name user))
(defn get-email [user] (:email user))
(defn get-age [user] (:age user))

(defn save-user [user]
  (println "Saving to DB:" (get-name user) (get-email user) (get-age user)))

(defn send-welcome-email [user]
  (println "Sending email to:" (get-email user)))

(defn track-user [user]
  (println "Tracking new user:" (get-name user) (get-age user)))

(defn create-user [name email age]
  (let [user (make-user {:name name :email email :age age})]
    (save-user user)
    (send-welcome-email user)
    (track-user user)))
    
(comment
  (create-user "Alice" "alice@example.com" 30))
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

``` clojure
(def user
  {:profile {:contact {:email "user@example.com"}}})

(defn get-user-email [user]
  (-> user :profile :contact :email))

(println (get-user-email user))
```

* __Refactoring:__ Avoid chaining multiple calls to access deeply nested data structures. Instead, extract intermediate lookups into separate helper functions. This reduces coupling to internal representations, improves readability, and makes the code easier to update if the structure changes.

``` clojure
(def user
  {:profile {:contact {:email "user@example.com"}}})

(defn user-profile [user]
  (:profile user))

(defn user-contact [profile]
  (:contact profile))

(defn user-email [contact]
  (:email contact))

(defn get-user-email [user]
  (-> user
      user-profile
      user-contact
      user-email))

(println (get-user-email user))
```

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

* __Refactoring:__ When a function only serves as a pass-through to another function (e.g., wrapping map, filter, or similar without adding any new behavior), it introduces unnecessary indirection. It's better to remove the intermediary and use the higher-order function directly. This simplifies the code, makes dependencies more explicit, and improves maintainability.

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

* __Description:__ This code smell occurs when functions, modules, or data structures become overly dependent on each other's internal representations or behaviors. This tight coupling reduces modularity, complicates maintenance, and makes it harder to evolve or reason about the system independently.

* __Example:__ 

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

* __Description:__ This code smell occurs when two or more distinct programming paradigms are used together in a way that forces interoperability. The blending of approaches — such as functional and imperative or object-oriented and procedural — can introduce confusion, reduce clarity, and complicate the design, as different paradigms impose conflicting constraints and expectations.

* __Example:__ 

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

* __Description:__ This code smell occurs when an application wraps a third-party library with its own functions or abstractions, often obscuring or complicating the library's usage.

* __Example:__

``` clojure
(ns examples.smells.library-locker
  (:require [clj-http.client :as client]))

(defn do-get [url]
  (client/get url))

(defn do-post [url data]
  (client/post url {:body data}))

(defn fetch-data []
  (do-get "https://httpbin.org/get"))

(defn send-data [info]
  (do-post "https://httpbin.org/post" info))

(println (fetch-data))
(println (send-data "test"))
```

* __Refactoring:__ Remove unnecessary wrappers and rely directly on the third-party library. This improves clarity, avoids indirection, and keeps the code simpler and easier to maintain.

``` clojure
(ns examples.refactored.library-locker-refactored
  (:require [clj-http.client :as client]))

(defn fetch-data []
  (client/get "https://httpbin.org/get"))

(defn send-data [info]
  (client/post "https://httpbin.org/post" {:body info}))

(println (fetch-data))
(println (send-data "test"))
```

# Functional-related Smells

## Overabstracted Composition

* __Description:__ This code smell occurs when excessive use of function composition (combining multiple functions into a single one) and partial application (fixing some arguments of a function to create a new one) makes the code overly abstract, sacrificing readability and maintainability. While function composition is a powerful tool in functional programming, overusing it can lead to deeply nested expressions that obscure the data flow.

* __Example:__
``` clojure
(defn get-user [data] (:user data))
(defn get-email [user] (:email user))
(defn trim [s] (str/trim s))
(defn lower [s] (str/lower-case s))
(def domain (comp second (partial split-at "@")))

;; Compose everything into a pipeline
(def process-email
  (comp
    domain
    lower
    trim
    get-email
    get-user))

(defn describe-user [data]
  (str "Domain: " (process-email data)))

(comment
  (describe-user {:user {:email "  Bob@Example.org  "}})
)
```

* __Refactoring:__ Replace chains of tiny composed or partially applied functions with more explicit and readable steps. Use `let` bindings or threading macros (`->`, `->>`) to clarify how data flows through each transformation.

``` clojure
(defn extract-domain [data]
  (let [email (get-in data [:user :email])
        cleaned (-> email str/trim str/lower-case)
        parts   (str/split cleaned #"@")]
    (second parts)))

(defn describe-user [data]
  (str "Domain: " (extract-domain data)))

(comment
  (describe-user {:user {:email "  Bob@Example.org  "}})
)
```


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
(ns examples.smells.inappropriate-collection)

(def people
  [{:person/name "Fred"}
   {:person/name "Ethel"}
   {:person/name "Lucy"}])

(defn person-in-people?
  [person people]
  (some #(= person (:person/name %)) people))

(println (boolean (person-in-people? "Fred" people)))
(println (boolean (person-in-people? "Alice" people)))
```

* __Refactoring:__ Scanning a sequential collection to find an item by a key is inefficient and obscures intent. Use an associative structure like group-by to enable fast, direct access.

``` clojure
(def people
  [{:person/name "Fred"}
   {:person/name "Ethel"}
   {:person/name "Lucy"}])

(def collected-people
  (into {} (map (fn [p] [(:person/name p) p]) people)))

(println (contains? collected-people "Fred")) 
(println (contains? collected-people "Alice"))
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

## External Data Coupling

* __Description:__ This code smell occurs when utilizing raw data from external sources as-is within your application, leading to tight coupling between your code and the external data structure. It is about how you model and transform external information.

* __Example:__
``` clojure
;; Raw external data received from an API or other system
(def raw-user-data
  {:user_name "alice"
   :user_age 30
   :user_email "alice@example.com"})

(defn process-user [raw-user]
  (println "Welcome," (:user_name raw-user))
  (println "Your email is:" (:user_email raw-user))
  (if (> (:user_age raw-user) 18)
    (println "You are an adult.")
    (println "You are a minor.")))

(comment
  (process-user raw-user-data))
```

* __Refactoring:__ Transform raw external data into a well-defined internal representation as early as possible. Encapsulate this transformation in a single function or module. Then, ensure the rest of your application interacts only with the internal structure.

``` clojure
(def raw-user-data
  {:user_name "alice"
   :user_age 30
   :user_email "alice@example.com"})

;; Transform external data into an internal model
(defn transform-user [external-user]
  {:name  (:user_name external-user)
   :age   (:user_age external-user)
   :email (:user_email external-user)})

(defn process-user [user]
  (println "Welcome," (:name user))
  (println "Your email is:" (:email user))
  (if (> (:age user) 18)
    (println "You are an adult.")
    (println "You are a minor.")))

(comment
  (let [user (transform-user raw-user-data)]
    (process-user user)))
```


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


## Hidden Side Effects

* __Description:__ This code smell occurs when functions perform side effects—such as I/O operations, state mutations, or logging—without making them explicit in their name, structure, or usage context. In functional programming, clarity around purity is essential for reasoning and testing. 

* __Example:__
```clojure
(defn greet-user [user]
  ;; Hidden side effect: printing during map
  (println "Hello," (:name user))
  (str "Greeted " (:name user)))

(defn greet-users [users]
  (map greet-user users))

(let [users [{:name "Alice"} {:name "Bob"} {:name "Carol"}]]
  (greet-users users))
```

* __Refactoring:__ Move side-effecting operations—such as `println`, logging, or I/O—outside of lazy or pure constructs like `map` or `filter`, and use `doseq` or `run!` for explicit sequencing. Clearly signal side effects by naming such functions with a `!` suffix, and keep pure functions strictly side-effect-free to improve clarity, testability, and reasoning.

```clojure
(defn greet-user! [user]
  ;; Side effect now explicit and named
  (println "Hello," (:name user)))

(defn greet-users! [users]
  ;; Use doseq for side effects
  (doseq [user users]
    (greet-user! user)))

(let [users [{:name "Alice"} {:name "Bob"} {:name "Carol"}]]
  (greet-users! users))
```


## Explicit Recursion

* __Description:__ This code smell occurs when explicit recursion is used instead of higher-order functions like `map`, `reduce`, or `filter`, which provide more concise and idiomatic solutions. Recursion should be reserved for cases where no suitable higher-level abstraction is available.

* __Example:__
```clojure
(defn double-nums [nums]
  (if (empty? nums)
    '()
    (cons (* 2 (first nums)) (double-nums (rest nums)))))

(double-nums [1 2 3 4])
```

* __Refactoring:__ Replace manual recursion with higher-order functions like `map`, `reduce`, or `filter`. These abstractions make code more concise, idiomatic, and easier to reason about.

```clojure
(defn double-nums [nums]
  (map #(* 2 %) nums))

(double-nums [1 2 3 4])
```


## Reinventing the Wheel

* __Description:__ This code smell occurs when developers reimplement functionality that is already provided by the language’s standard, idiomatic constructs—particularly in the context of sequence processing. Instead of using expressive built-in functions like `map`, `mapcat`, `filter`, `remove`, `keep`, `second`, or `ffirst`, code may rely on verbose anonymous functions, deeply nested calls, or manual iteration patterns. These reinventions not only obscure the original intent of the code but also introduce unnecessary complexity, reduce readability, and increase the potential for subtle bugs.

* __Example:__
```clojure
(defn process-data [data]
  (let [filtered (filter (fn [x] (not (nil? (get x :active)))) data)
        names (map (fn [x] (get x :name)) filtered)
        seconds (map (fn [x] (nth x 1)) (map vec (map :tags filtered)))
        flat-tags (apply concat (map (fn [x] (:tags x)) filtered))]
    {:names names
     :seconds seconds
     :flat-tags flat-tags}))

(process-data
 [{:name "Alice" :active true  :tags ["admin" "editor"]}
  {:name "Bob"   :active false :tags ["viewer" "editor"]}
  {:name "Carol" :active true  :tags ["editor" "reviewer"]}])
```

* __Refactoring:__ Replace custom anonymous functions and manual logic with Clojure’s built-in sequence functions such as `map`, `mapcat`, `filter`, `second`, or keyword-as-function idioms. These idioms express intent more clearly, reduce verbosity, and produce safer, more maintainable code.

```clojure
(defn process-data [data]
  (let [filtered (filter :active data)
        names (map :name filtered)
        seconds (map second (map :tags filtered))
        flat-tags (mapcat :tags filtered)]
    {:names names
     :seconds seconds
     :flat-tags flat-tags}))

(process-data
 [{:name "Alice" :active true  :tags ["admin" "editor"]}
  {:name "Bob"   :active false :tags ["viewer" "editor"]}
  {:name "Carol" :active true  :tags ["editor" "reviewer"]}])
```


## Positional Return Values

* __Description:__ This code smell occurs when functions return multiple values using positional collections such as vectors or lists, rather than explicitly labeled maps. While concise, this practice relies on the consumer to remember the semantic meaning of each position. It introduces hidden contracts and makes the code harder to read, understand, and maintain—especially as the number of return values grows. 

* __Example:__
```clojure
(defn sieve
  [p xs]
  [(filter p xs) (remove p xs)])

(first (sieve even? (range 9)))
```

* __Refactoring:__ Refactor functions that return multiple values as positional collections into functions that return maps with descriptive keys. This makes the meaning of each returned value explicit, reducing cognitive load on consumers and minimizing errors related to incorrect indexing.

```clojure
(defn sieve
  [p xs]
  {:true (filter p xs) :false (remove p xs)})

(:true (sieve even? (range 9)))
```


# Clojure-specific Smells

## Unnecessary Macros

* __Description:__ This code smell occurs when macros are used in situations where simpler, more conventional solutions—such as functions or existing language constructs—would suffice. While macros offer powerful metaprogramming capabilities, their overuse introduces unnecessary abstraction and complexity. This can obscure the code’s intent, make debugging more challenging and reducing maintainability.

* __Example:__
```clojure
(defmacro unless [test & body]
  `(if (not ~test)
     (do ~@body)))

(unless false
  (println "This runs because test is false"))
```

* __Refactoring:__ Avoid macros when a simple function or existing language construct achieves the same goal. Replace macros with functions that clearly express the intent and minimize metaprogramming complexity. Use them only when necessary to extend language syntax or perform compile-time transformations that cannot be done with functions.

```clojure
(defn unless-fn [test & body]
  (when (not test)
    (doseq [expr body] expr)))

(unless-fn false
  (println "This runs because test is false"))
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


## Namespaced Keys Neglect

* __Description:__ This code smell occurs when developers rely on unqualified keywords (e.g., `:id`, `:name`) instead of using namespaced keywords (e.g., `:user/id`, `:order/name`). While seemingly harmless, this practice leads to ambiguity, increased risk of key collisions, and reduced semantic clarity — particularly in large or modular systems.

* __Example:__
```clojure
(def user {:id 1 :name "Alice"})
(def order {:id 101 :name "Order-101"})

(println (:id user))    ;; 1
(println (:id order))   ;; 101
```

* __Refactoring:__ Always use namespaced keywords to clearly distinguish data domains and reduce ambiguity. This practice prevents key collisions in complex systems, improves code readability by providing explicit context, and makes it easier to reason about and maintain data structures across different modules or libraries.

```clojure
(def user {:user/id 1 :user/name "Alice"})
(def order {:order/id 101 :order/name "Order-101"})

(println (:user/id user))    ;; 1
(println (:order/id order))  ;; 101
```

## Improper Emptiness Check

* __Description:__ This code smell occurs when developers use verbose or less idiomatic constructs—such as (`not (empty? x)`)—to determine whether a collection is non-empty, instead of leveraging the more concise and expressive idiom (`seq x`). In Clojure, the concept of emptiness is nuanced: `nil` is considered empty, sequences can be infinite or lazy, and realization may matter. Using `seq` not only simplifies the check but also aligns with Clojure’s idiomatic style, improving readability and avoiding redundant negation or abstraction layers.

* __Example:__
```clojure
(defn process-if-not-empty [coll]
  (when (not (empty? coll))
    (str "Processing: " coll)))

(defn process-if-empty [coll]
  (when (= 0 (count coll))
    "Empty collection detected"))

[(process-if-not-empty [])
 (process-if-not-empty [1 2])
 (process-if-empty [])
 (process-if-empty [1])]
```

* __Refactoring:__ Avoid verbose or redundant checks like `(not (empty? x))` and `(= 0 (count x))`. Instead, use `(seq x)` to test for non-emptiness and `(empty? x)` to test for emptiness. These forms are shorter, idiomatic, and clearer in expressing intent, while also avoiding unnecessary collection realization and reducing potential confusion with nil handling.

```clojure
(defn process-if-not-empty [coll]
  (when (seq coll)
    (str "Processing: " coll)))

(defn process-if-empty [coll]
  (when (empty? coll)
    "Empty collection detected"))

[(process-if-not-empty [])
 (process-if-not-empty [1 2])
 (process-if-empty [])
 (process-if-empty [1])]
```


## Accessing non-existent Map Fiels

* __Description:__ This code smell occurs when code accesses map keys that may not exist, relying on nil as a default return without explicit handling. In Clojure, (`get m :key`) returns `nil` both when the key is missing and when it is explicitly associated with `nil`, which can obscure intent and lead to subtle bugs. Since Clojure maps treat `nil` as both a value and a signal of absence, the distinction between "missing" and "present but empty" becomes ambiguous. 

* __Example:__
```clojure
(defn welcome-message [user]
  (str "Welcome, " (:name user)))

(welcome-message {:id 42})
(welcome-message {:id 43 :name nil})
```

* __Refactoring:__ To eliminate this smell, first try to avoid inserting `nil` values into maps when you have control over their construction—using utilities like `assoc-some` or `prune-nils` can help. When reading from a map, avoid assuming `nil` means absence. Instead, use (`contains? m :key`) to test key presence explicitly. This clarifies intent and prevents subtle bugs caused by missing fields or keys deliberately set to nil.

```clojure
(defn welcome-message [user]
  (if (contains? user :name)
    (str "Welcome, " (:name user))
    "Name not provided"))

[(welcome-message {:id 42})
 (welcome-message {:id 44 :name "Alice"})]
```


## Unnecessary `into`

* __Description:__ This code smell occurs when the `into` function is used in situations where more concise or idiomatic alternatives exist, leading to unnecessarily verbose or inefficient code. While into is useful for combining collections, it is often misused for simple type transformations—such as (`into [] coll`) instead of `vec`.

* __Example:__
```clojure
(def users [{:id 1 :active true} {:id 2 :active false} {:id 3 :active true}])

(defn active-ids [users]
  (into [] (map :id (filter :active users))))

(defn id-set [users]
  (into #{} (map :id users)))

(defn rename-keys [m]
  (into {} (map (fn [[k v]] [(keyword (str "new-" (name k))) v]) m)))

(comment
  (active-ids users) ;; => [1 3]
  (id-set users)     ;; => #{1 2 3}
  (rename-keys {:a 1 :b 2}) ;; => {:new-a 1, :new-b 2}
)
```

* __Refactoring:__ To eliminate this smell, replace uses of `into []` with `vec`, and `into #{}` with `set`, for clarity and intent. When transforming maps, prefer `reduce-kv` to avoid the extra indirection of (`into {} (map ...)`). These alternatives reduce verbosity, improve performance, and align with idiomatic Clojure practices.

```clojure
(def users [{:id 1 :active true} {:id 2 :active false} {:id 3 :active true}])

(defn active-ids [users]
  (vec (map :id (filter :active users))))

(defn id-set [users]
  (set (map :id users)))

(defn rename-keys [m]
  (reduce-kv (fn [acc k v] (assoc acc (keyword (str "new-" (name k))) v)) {} m))

(comment
  (active-ids users) ;; => [1 3]
  (id-set users)     ;; => #{1 2 3}
  (rename-keys {:a 1 :b 2}) ;; => {:new-a 1, :new-b 2}
)
```


## Conditional Build-Up

* __Description:__ This code smell occurs when a state is incrementally constructed through a series of `let`, `if`, and `assoc` expressions, leading to verbose and imperative-style code. Rather than clearly expressing the transformation logic, this pattern scatters conditional state mutations across multiple branches, making it harder to reason about the overall flow. 

* __Example:__
```clojure
(defn f0 [in] (* in 10))
(defn f1 [in] (+ in 1))
(defn f2 [in] (- in 1))
(defn p1 [in] (pos? in))
(defn p2 [in] (even? in))

(defn foo [in]
  (let [m {:k0 (f0 in)}
        m (if (p1 in) (assoc m :k1 (f1 in)) m)
        m (if (p2 in) (assoc m :k2 (f2 in)) m)]
    m))

(foo 2)
```

* __Refactoring:__ Replace imperative-style stepwise construction (via repeated  `let`/`if`/`assoc`) with `cond->`, which cleanly threads conditional transformations. This improves clarity by co-locating conditions with the associated changes, avoids repetitive rebinding, and communicates intent more directly.

```clojure
(defn f0 [in] (* in 10))
(defn f1 [in] (+ in 1))
(defn f2 [in] (- in 1))
(defn p1 [in] (pos? in))
(defn p2 [in] (even? in))

(defn foo
  [in]
  (cond-> {:k0 (f0 in)}
    (p1 in) (assoc :k1 (f1 in))
    (p2 in) (assoc :k2 (f2 in))))

(foo 2)
```


## Verbose Checks

* __Description:__ This code smell arises when developers manually implement common checks (such as checking if a number is zero, positive, or negative), when Clojure already provides clear, idiomatic functions that do the same. This results in verbose and less readable code, and misses an opportunity to leverage Clojure's built-in abstractions for clarity.

* __Example:__
```clojure
(defn number-type [n]
  (cond
    (= n 0) :zero
    (< 0 n) :positive
    (> 0 n) :negative))

(number-type 0)
;; => :zero

(number-type 5)
;; => :positive

(number-type -3)
;; => :negative
```

* __Refactoring:__ Replace explicit numeric, boolean and nil comparisons like `(= n 0)`, `(< 0 n)`, `(> 0 n)`, `(= true x)`, `(= false x)` or `(= nil x)` with Clojure’s idiomatic predicates: `zero?`, `pos?`, `neg?`, `true?`, `false?` or `nil?`. These functions not only make the code more concise and expressive but also improve semantic clarity.

```clojure
(defn number-type [n]
  (cond
    (zero? n) :zero
    (pos? n)  :positive
    (neg? n)  :negative))

(number-type 0)

(number-type 5)

(number-type -3)
```


## Production `doall`

* __Description:__ This code smell occurs when the `doall` function is used in production code to force realization of lazy sequences, often to trigger side effects or avoid deferred evaluation. While `doall` can be useful in REPL experimentation, its use in production undermines one of Clojure’s core strengths: laziness. For large or infinite sequences, this can lead to memory spikes and unpredictable performance. In production contexts, doall often signals poor abstraction choices and should prompt reconsideration of the control flow or evaluation strategy.

* __Example:__
```clojure
(defn print-evens []
  (doall (map #(println %) (filter even? (range 1000)))))

(print-evens)
```

* __Refactoring:__ Replace `doall` with explicit constructs like `doseq`, `run!`, or `dorun` when your intent is to trigger side effects or consume a sequence. These constructs clearly communicate your purpose and avoid the accidental full realization of large or infinite sequences, which can cause memory issues or performance degradation. In most production scenarios, laziness should be preserved or explicitly managed through more idiomatic control structures. 

```clojure
(defn print-evens []
  (doseq [n (filter even? (range 1000))]
    (println n)))

(print-evens)
```


## Redundant `do` block

* __Description:__ This code smell occurs when developers wrap expressions in an explicit (`do ...`) block inside constructs that already support implicit sequencing, such as `let`, `when`, `if`, `fn`, `try`, `loop`, and others. This redundant use of do adds no semantic value but introduces unnecessary syntax, making the code appear more complex and imperative than it actually is. 

* __Example:__
```clojure
(defn process-item [x]
  (when (pos? x)
    (do
      (println "Processing:" x)
      (* x 2))))

(process-item 2)
```

* __Refactoring:__ Remove `do` blocks that are nested inside forms already capable of handling multiple expressions. These constructs perform implicit sequencing, so wrapping their bodies in a do block adds no functional value and increases syntactic noise.

```clojure
(defn process-item [x]
  (when (pos? x)
    (println "Processing:" x)
    (* x 2)))

(process-item 2)
```


## Thread Ignorance

* __Description:__ This code smell occurs when developers avoid or misuse Clojure’s threading macros (`->`, `->>`, `some->`, `cond->`) in scenarios where they would provide clearer, more idiomatic data flow. Instead of leveraging threading to express stepwise transformations, code may fall back to repetitive bindings, deeply nested `let` or `when-let` forms, or manually sequenced function calls. This results in verbose, harder-to-follow logic and obscures the intent behind each transformation.

* __Example:__
```clojure
(defn transform [xs]
  (let [step1 (map inc xs)
        step2 (filter even? step1)
        step3 (reduce + step2)]
    step3))

(transform [1 2 3 4])
```

* __Refactoring:__ Favor Clojure's threading macros when performing stepwise transformations on data. These macros eliminate unnecessary intermediate bindings and reduce nesting, making the data flow explicit and linear.

```clojure
(defn transform [xs]
  (->> xs
       (map inc)
       (filter even?)
       (reduce +)))

(transform [1 2 3 4])
```

## Nested Forms

* __Description:__ This code smell occurs when multiple binding or iteration forms—such as `let`, `when-let`, `if-let`, or `doseq`—are unnecessarily nested instead of being combined in a single, flat form. While technically valid, this nesting introduces extra indentation and structural complexity without adding semantic value. It obscures the relationships between bindings, increases visual noise, and makes the code harder to read and reason about.

* __Example:__
```clojure
(defn process [user]
  (let [profile (:profile user)]
    (when profile
      (let [address (:address profile)]
        (when address
          (let [city (:city address)]
            (str "City: " city)))))))

(process {:profile {:address {:city "Recife"}}})
```

* __Refactoring:__ Combine multiple bindings and conditional checks into a single, flat form using constructs like `when-let`, `if-let`, or multi-binding `let` expressions. Instead of creating a new nested block for each intermediate extraction or check, prefer a structure that expresses sequential dependencies inline, making the data flow easier to follow.

```clojure
(defn process [user]
  (when-let [city (some-> user :profile :address :city)]
    (str "City: " city)))

(process {:profile {:address {:city "Recife"}}})
```

## Direct usage of `clojure.lang.RT`

* __Description:__ This code smell occurs when Clojure code directly invokes methods from the `clojure.lang.RT` class, such as `clojure.lang.RT/iter`, to perform operations that are not exposed through the public Clojure API. The RT class is part of Clojure's internal implementation and is not intended for direct use in application code. Directly invoking methods from this class can lead to fragile code that is susceptible to breakage with future updates to the language.

* __Example:__
```clojure
(defn print-all [xs]
  (let [it (clojure.lang.RT/iter xs)]
    (loop []
      (when (.hasNext it)
        (println (.next it))
        (recur)))))

(print-all [1 2 3])
```

* __Refactoring:__ Avoid using `clojure.lang.RT` directly and prefer public sequence operations like `doseq`, `map`, or `reduce`. These idiomatic constructs are more readable, safe, and portable across Clojure versions.

```clojure
(defn print-all [xs]
  (doseq [x xs]
    (println x)))

(print-all [1 2 3])
```
