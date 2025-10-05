# Catalog of traditional smells in Clojure

This repository is a companion to the [Clojure-specific code smells catalog](/README.md), focusing on traditional code smells from the literature, especially those proposed by Fowler and Beck. Although originally associated with object-oriented languages, these smells are revisited here in the context of the Clojure ecosystem.

The catalog adopts the same structure and methodology as the main catalog, including a description, code example, source, and supporting excerpt for each smell.

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
  * [Deferred anti-pattern](#deferred-anti-pattern)
  * [Promise-callback hell](#promise-callback-hell)

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

* __Sources and Excerpts:__

  -  **Source:** [Reddit - Functional programming anti-patterns?](https://www.reddit.com/r/Clojure/comments/gf9vl0/functional_programming_antipatterns/) <br>
    **Excerpt:** “[...] you're hurting performance (at least on the JVM) when you have large methods [...]. To me, it makes sense to break this down or try to simplify it.”


## Long Parameter List

* __Description:__ This code smell occurs when a function or method accepts an excessive number of parameters. Long parameter lists make functions harder to understand, more error-prone to call, and more difficult to refactor.

* __Example:__

```clojure
(defn register-new-user
  [username password email phone age gender location interests newsletter-opt-in referred-by]
  {:username username
   :password password
   :email email
   :phone phone
   :age age
   :gender gender
   :location location
   :interests interests
   :newsletter newsletter-opt-in
   :referral referred-by})
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

* __Sources and Excerpts:__

  -  **Source:** [The Library Locker - An Antipattern](https://thomascothran.tech/2023/08/library-locker/) <br>
    **Excerpt:** “The “Library Locker” is a common anti-pattern for incorporating third party libraries into an application”

## Deferred Anti-pattern

* __Description:__ Occurs when code manually creates a new Promise object (often using a pattern like `Promise.pending()` or `$q.defer()`) to wrap a function that is already asynchronous and returns a Promise or uses a standard callback convention. This superfluous wrapping violates the principle of Promise chaining, destroys the proper propagation of errors and rejections, and significantly increases complexity and boilerplate without adding any functional value.

* __Example:__

``` clojure
;; Example from source
(let [p (promise)]
   ...
   (if something (deliver p :good) (fail p (Exception. ...)))
   ...
  p)
```

* __Sources and Excerpts:__

  -  **Source:** [Promise Anti Patterns](https://github.com/petkaantonov/bluebird/wiki/Promise-anti-patterns#the-deferred-anti-pattern) <br>
    **Excerpt:** “This is the most common anti-pattern. It is easy to fall into this when you don't really understand promises and think of them as glorified event emitters or callback utility.”
  -  **Source:** [Issue](https://github.com/funcool/promesa/issues/60) <br>
    **Excerpt:** “Because the "deferred" is an anti pattern.”

## Promise-Callback Hell

* __Description:__ Occurs when an asynchronous function returns a Promise or Deferred object and simultaneously accepts a callback function as a final argument for the same successful resolution. This creates a brittle dual asynchronous contract, forcing an unnecessarily large function arity, and leads to boilerplate code where the callback is merely passed through to the Promise's `.then()` method. This pattern complicates code flow, prevents the use of superior, linearized Promise composition idioms, and often indicates a function that is transitioning poorly from a callback-based API.

* __Example:__

``` clojure
;; Not from source
(defn save-credentials-smelly [user callback]
  (let [result-promise (my-async-op user)]
    (.then result-promise callback)
    result-promise))
```

* __Sources and Excerpts:__

  -  **Source:** [Pull Request](https://github.com/status-im/status-mobile/pull/15495) <br>
    **Excerpt:** “`save-credentials` should not receive a `callback` argument. This is unnecessary for a function that returns a promise. It's actually a bad practice if the callback is just passed without modification to the final `.then` call.
”