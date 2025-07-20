# Table of Smells
<!-- no toc -->
* [Traditional smells](#traditional-smells)
  * [Long function](#long-function)
  * [Long parameter list](#long-parameter-list)
  * [Divergent change](#divergent-change)
  * [Shotgun surgery](#shotgun-surgery)
  * [Inappropriate intimacy](#inappropriate-intimacy)
  * [Comments](#comments)
  * [Mixed paradigms](#mixed-paradigms)
  * [Library locker](#library-locker)

# Traditional Smells

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

* __Sources and Excerpts:__

  -  **Source:** [Reddit - Functional programming anti-patterns?](https://www.reddit.com/r/Clojure/comments/gf9vl0/functional_programming_antipatterns/) <br>
    **Excerpt:** “[...] you're hurting performance (at least on the JVM) when you have large methods [...]. To me, it makes sense to break this down or try to simplify it.”


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

* __Sources and Excerpts:__

  -  **Source:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/) <br>
    **Excerpt:** “Avoid An Explosion In Input Arguments”

  -  **Source:** [Reddit - Functional programming anti-patterns?](https://www.reddit.com/r/Clojure/comments/gf9vl0/functional_programming_antipatterns/) <br>
    **Excerpt:** “Another pattern that I have seen, and this was specific to Clojure, is functions which take a lot of parameters, maybe ten or more.”


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

* __Sources and Excerpts:__

  -  **Source:** [Refactoring tests using builder functions in Clojure/ClojureScript](https://codesai.com/posts/2016/10/refactoring-tests-using-builder-functions-in-clojure-clojurescript) <br>
    **Excerpt:** “Nearly any change in the representation of those data will have a big impact on the tests code”

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

* __Sources and Excerpts:__

  -  **Source:** [Refactoring tests using builder functions in Clojure/ClojureScript](https://codesai.com/posts/2016/10/refactoring-tests-using-builder-functions-in-clojure-clojurescript) <br>
    **Excerpt:** “Nearly any change in the representation of those data will have a big impact on the tests code”

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

* __Sources and Excerpts:__

  -  **Source:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/) <br>
    **Excerpt:** “To compose for a sequential use case we could have used transducers. Now we are tied to a sequential concretion”

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

* __Sources and Excerpt:__

  -  **Source:** [Are comments a code smell? Yes! No? It Depends.](https://pragtob.wordpress.com/2017/11/14/are-comments-a-code-smell-yes-no-it-depends/) <br>
    **Excerpt:** “There is so much more that you can do to make your code more readable instead of resorting to a comment. Comments should be a last resort”.

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

* __Sources and Excerpts:__

  -  **Source:** [Functional programming anti-patterns?](https://www.reddit.com/r/Clojure/comments/gf9vl0/functional_programming_antipatterns/?rdt=57161) <br>
    **Excerpt:** “And a related antipattern is to write imperative style code in one big function”.
  -  **Source:** [How to refactor a Java singleton to Clojure?](https://softwareengineering.stackexchange.com/questions/219780/how-to-refactor-a-java-singleton-to-clojure)<br>
    **Excerpt:** “So if you want to learn how to write good Clojure code, you'll have to learn how to avoid (most) mutable state at some point”.

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

* __Sources and Excerpts:__

  -  **Source:** [The Library Locker - An Antipattern](https://thomascothran.tech/2023/08/library-locker/) <br>
    **Excerpt:** “The “Library Locker” is a common anti-pattern for incorporating third party libraries into an application”
