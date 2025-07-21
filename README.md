# Catalog of Clojure-related code smells

This repository presents a catalog of code smells relevant to the Clojure ecosystem. The methodology follows the [original study](https://doi.org/10.1007/s10664-023-10343-6) on Elixir by Vegi & Valente (2023), with adaptations for the specifics of Clojure.

In a nutshell, we analyzed developer discussions from forums, blogs, and other practitioner sources to identify recurring problematic patterns that experienced Clojure developers frequently encounter and discuss. The catalog was compiled from real-world examples shared by practitioners across various online platforms.

The identified smells were organized in this repository, each with a description, code example, source and excerpt from source indicating the smell.

This catalog reflects the current stage of our ongoing study. We plan to expand it in future phases, following the model of the Elixir work. Contributions are welcome via issues and pull requests.

# Table of Smells
<!-- no toc -->
- [Clojure-specific Smells](#clojure-specific-smells)
  - [Unnecessary Macros](#unnecessary-macros)
  - [Immutability Violation](#immutability-violation)
  - [Namespaced Keys Neglect](#namespaced-keys-neglect)
  - [Improper Emptiness Check](#improper-emptiness-check)
  - [Accessing non-existent Map Fiels](#accessing-non-existent-map-fiels)
  - [Unnecessary `into`](#unnecessary-into)
  - [Conditional Build-Up](#conditional-build-up)
  - [Verbose Checks](#verbose-checks)
  - [Production `doall`](#production-doall)
  - [Redundant `do` block](#redundant-do-block)
  - [Thread Ignorance](#thread-ignorance)
  - [Nested Forms](#nested-forms)
  - [Direct usage of `clojure.lang.RT`](#direct-usage-of-clojurelangrt)
- [Functional-related Smells](#functional-related-smells)
  - [Overabstracted Composition](#overabstracted-composition)
  - [Overuse Of High-Order Functions](#overuse-of-high-order-functions)
  - [Trivial Lambda](#trivial-lambda)
  - [Deeply-nested Call Stacks](#deeply-nested-call-stacks)
  - [Inappropriate Collection](#inappropriate-collection)
  - [Premature Optimization](#premature-optimization)
  - [Lazy Side Effects](#lazy-side-effects)
  - [External Data Coupling](#external-data-coupling)
  - [Inefficient Filtering](#inefficient-filtering)
  - [Hidden Side Effects](#hidden-side-effects)
  - [Explicit Recursion](#explicit-recursion)
  - [Reinventing the Wheel](#reinventing-the-wheel)
  - [Positional Return Values](#positional-return-values)

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

* __Sources and Excerpts:__

  -  **Source:** [Forum - Structuring Clojure applications](https://news.ycombinator.com/item?id=34052268)<br>
      **Excerpt:** “Using macros when regular functions would do is a good example of that. It is absolutely possible to write impenetrable Clojure if you start doing weird things just because you can.”

## Immutability Violation

* __Description:__ This code smell Immutability occurs when mutable state is used in a language or paradigm that emphasizes immutability (such as Clojure), leading to side effects, reduced predictability, and harder-to-maintain code.

* __Example:__

``` clojure
;; Example from source
(def countries (do-get-countries))

(defn update-country [country]
  (def countries (assoc countries (:name country) country)))

(update-country {:name "Brazil" :pop 210})
```

* __Sources and Excerpts:__

  -  **Source:** [Forum - How to refactor a Java singleton to Clojure?](https://softwareengineering.stackexchange.com/questions/219780/how-to-refactor-a-java-singleton-to-clojure)<br>
      **Excerpt:** “Mutable state totally destroys this concept, and with it, the advantages of pure code. Clojure doesn't force you to be pure, but it certainly makes it easy to do so”

## Namespaced Keys Neglect

* __Description:__ This code smell occurs when developers rely on unqualified keywords (e.g., `:id`, `:name`) instead of using namespaced keywords (e.g., `:user/id`, `:order/name`). While seemingly harmless, this practice leads to ambiguity, increased risk of key collisions, and reduced semantic clarity — particularly in large or modular systems.

* __Example:__
```clojure
(def user {:id 1 :name "Alice"})
(def order {:id 101 :name "Order-101"})

(println (:id user))    ;; 1
(println (:id order))   ;; 101
```

* __Sources and Excerpts:__

  -  **Source:** [Reddit - Functional programming anti-patterns?](https://www.reddit.com/r/Clojure/comments/gf9vl0/functional_programming_antipatterns/)<br>
      **Excerpt:** “Namespaced keywords are good. Use them.”

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

* __Sources and Excerpts:__

  -  **Source:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/)<br>
      **Excerpt:** “Don't use (not (empty? x))!”


## Accessing non-existent Map Fiels

* __Description:__ This code smell occurs when code accesses map keys that may not exist, relying on nil as a default return without explicit handling. In Clojure, (`get m :key`) returns `nil` both when the key is missing and when it is explicitly associated with `nil`, which can obscure intent and lead to subtle bugs. Since Clojure maps treat `nil` as both a value and a signal of absence, the distinction between "missing" and "present but empty" becomes ambiguous.

* __Example:__
```clojure
(defn welcome-message [user]
  (str "Welcome, " (:name user)))

(welcome-message {:id 42})
(welcome-message {:id 43 :name nil})
```

* __Sources and Excerpts:__

  -  **Source:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/)<br>
      **Excerpt:** “Clojure maps are collections, not slots. Combined with nil's meaning being "nothing", nil values inside maps are confusing. [...] Try to avoid inserting nil values into a map.”


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

* __Sources and Excerpts:__

  -  **Source:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/)<br>
      **Excerpt:** “`into` is a pretty useful function, but one often abused. The (mis)usage of into can usually be broken to three distinct cases: Type Transformation, Map Mapping and Not Using the Transducer API.”


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

* __Sources and Excerpts:__

  -  **Source:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/)<br>
      **Excerpt:** “Conditional Build-Up”


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

* __Sources and Excerpts:__

  -  **Source:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/)<br>
      **Excerpt:** Numbers! and Truth Be Told sections


## Production `doall`

* __Description:__ This code smell occurs when the `doall` function is used in production code to force realization of lazy sequences, often to trigger side effects or avoid deferred evaluation. While `doall` can be useful in REPL experimentation, its use in production undermines one of Clojure’s core strengths: laziness. For large or infinite sequences, this can lead to memory spikes and unpredictable performance. In production contexts, doall often signals poor abstraction choices and should prompt reconsideration of the control flow or evaluation strategy.

* __Example:__
```clojure
(defn print-evens []
  (doall (map #(println %) (filter even? (range 1000)))))

(print-evens)
```

* __Sources and Excerpts:__

  -  **Source:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/)<br>
      **Excerpt:** “`doall` is a macro which forcefully realizes lazy sequences. It should not be used in production.”

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

* __Sources and Excerpts:__

  -  **Source:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/)<br>
      **Excerpt:** “Some expressions have implicit `do` blocks in them, making it unnecessary to use a `do` block.”

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

* __Sources and Excerpts:__

  -  **Source:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/)<br>
      **Excerpt:** “Avoid trivial threading [...] And remember to thread with style.”


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

* __Sources and Excerpts:__

  -  **Source:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/)<br>
      **Excerpt:** “Plenty of macros with binding forms don't need to be nested.”

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

* __Sources and Excerpts:__

  -  **Source:** [Forum - Is interop with clojure.lang.RT an anti-pattern in clojure? / consider adding iter to clojure.core](https://ask.clojure.org/index.php/10303/interop-clojure-pattern-clojure-consider-adding-iter-clojure)<br>
      **Excerpt:** “RT should be considered internal implementation and should not be called directly. Iterators, in general, are very un-clojurey. They are stateful and generally not concurrency friendly.”


# Functional-related Smells

## Overabstracted Composition

* __Description:__ This code smell occurs when excessive use of function composition (combining multiple functions into a single one) and partial application (fixing some arguments of a function to create a new one) makes the code overly abstract, sacrificing readability and maintainability. While function composition is a powerful tool in functional programming, overusing it can lead to deeply nested expressions that obscure the data flow.

* __Example:__

```clojure
;; Example from source
(def m
  {:one {:two {:three 3 :four 4 :five 5}}})
  (->> ((comp :two :one) m)
     ((juxt :three :five)))
```

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

* __Sources and Excerpts:__

   -  **Source:** [Reddit - Functional programming anti-patterns?](https://www.reddit.com/r/Clojure/comments/gf9vl0/functional_programming_antipatterns/)<br>
    **Excerpt:** “Overabundance of partial application and composition”

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

* __Sources and Excerpts:__

   -  **Source:** [Reddit - Functional programming anti-patterns?](https://www.reddit.com/r/Clojure/comments/gf9vl0/functional_programming_antipatterns/)<br>
    **Excerpt:** “Designing everything as higher order functions”

## Trivial Lambda

* __Description:__ This code smell occurs when anonymous functions (lambdas) are excessively used instead of named functions, reducing code clarity and reusability. This is especially problematic when lambda expressions are chained or become overly complex, making the code harder to read, understand, and maintain.

* __Example:__

```clojure
;; Example from source
(map #(f %) xs)
```

``` clojure
(defn square [x]
  (* x x))

(def numbers [1 2 3 4])

(println (map #(square %) numbers))
```

* __Sources and Excerpts:__

   -  **Source:** [Reddit - Functional programming anti-patterns?](https://www.reddit.com/r/Clojure/comments/gf9vl0/functional_programming_antipatterns/)<br>
    **Excerpt:** “Anonymous functions for everything”
  -  **Source:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/)<br>
  **Excerpt:** “Trivial Lambda”

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

* __Sources and Excerpts:__

   -  **Source:** [Reddit - Functional programming anti-patterns?](https://www.reddit.com/r/Clojure/comments/gf9vl0/functional_programming_antipatterns/)<br>
    **Excerpt:** “Each nested function call is a jump in context which makes it difficult for a reader to track, and it limits available information in the scope so it's generally less flexible.”

## Inappropriate Collection

* __Description:__ This code smell occurs when a data structure is used in a way that contradicts its intended purpose, leading to inefficient operations. For example, if a sequential collection is frequently scanned to locate elements by key, it likely indicates that an associative structure (e.g., a map or dictionary) would be more appropriate, improving both performance and clarity.

* __Example:__

```clojure
;; Example from source
(def people
  [{:person/name "Fred"}
   {:person/name "Ethel"}
   {:person/name "Lucy"}])

(defn person-in-people?
  [person people]
  (some #(= person (:person/name %)) people))

(person-in-people? "Fred" people);; => true
(person-in-people? "Bob" people) ;; => nil
```

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

* __Sources and Excerpts:__

   -  **Source:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/)<br>
    **Excerpt:** “If you find yourself scanning collections of maps looking for a map where a certain key has a certain value, your collection might be telling you it wants to be associative, not sequential”

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

* __Sources and Excerpts:__

   -  **Source:** [Clojure AntiPatterns: the with-retry macro](https://grishaev.me/en/clojure-with-retry/)<br>
    **Excerpt:** “There might be dozens of reasons when your request fails, and there is no way to recover. Instead of invoking a resource again and again, you must investigate what went wrong”.

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

* __Sources and Excerpts:__

   -  **Source:** [Defaulting to Transducers](https://dawranliou.com/blog/default-transducers/)<br>
    **Excerpt:** “There might be dozens of reasons when your request fails, and there is no way to recover. Instead of invoking a resource again and again, you must investigate what went wrong”

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

* __Sources and Excerpts:__

   -  **Source:** [Ep 108: Testify!](https://clojuredesign.club/episode/108-testify/)<br>
    **Excerpt:** “It's really tempting to use the external data as your working data.”

## Inefficient Filtering

* __Description:__ This code smell occurs when the data generator makes excessive use of filters (such as such-that) to constrain the generated values. Instead of genering only valid values directly, the generator creates a large number of values and filters them afterward, resulting in resource waste and potential performance issues.

* __Example:__

``` clojure
(require '[clojure.test.check.generators :as gen])

(def gen-even-int
  (gen/such-that even? gen/int))

(println (gen/sample gen-even-int 5))
```

* __Sources and Excerpts:__

   -  **Source:** [Ep 108: Testify!](https://clojuredesign.club/episode/108-testify/)<br>
    **Excerpt:** “It's really tempting to use the external data as your working data.


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

* __Sources and Excerpts:__

   -  **Source:** [Reddit - Functional programming anti-patterns?](https://www.reddit.com/r/Clojure/comments/gf9vl0/functional_programming_antipatterns/)<br>
    **Excerpt:** “Make your side-effects obvious”

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

* __Sources and Excerpts:__

   -  **Source:** [Reddit - Functional programming anti-patterns?](https://www.reddit.com/r/Clojure/comments/gf9vl0/functional_programming_antipatterns/)<br>
    **Insight:** “Using explicit recursion tends to be a code smell, there's a good chance that there's a higher order function that can do the job”

## Reinventing the Wheel

* __Description:__ This code smell occurs when developers reimplement functionality that is already provided by the language’s standard, idiomatic constructs—particularly in the context of sequence processing. Instead of using expressive built-in functions like `map`, `mapcat`, `filter`, `remove`, `keep`, `second`, or `ffirst`, code may rely on verbose anonymous functions, deeply nested calls, or manual iteration patterns. These reinventions not only obscure the original intent of the code but also introduce unnecessary complexity, reduce readability, and increase the potential for subtle bugs.

* __Example:__

```clojure
;; Example from source
(apply concat (map f xs))
```

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

* __Sources and Excerpts:__

    -  **Source:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/)<br>
    **Excerpt:** “Sometimes you need to concat the results of mapping. Using mapcat is an idiomatic option for this case.”

   -  **Source:** [Smelly Code](https://itsrainingmani.dev/blog/smelly-code/)<br>
    **Excerpt:** “I ended up using the (apply concat (map f xs)) construct to solve quite a few problems when an idiomatic option would have been to simply use the mapcat function.”

## Positional Return Values

* __Description:__ This code smell occurs when functions return multiple values using positional collections such as vectors or lists, rather than explicitly labeled maps. While concise, this practice relies on the consumer to remember the semantic meaning of each position. It introduces hidden contracts and makes the code harder to read, understand, and maintain—especially as the number of return values grows.

* __Example:__
```clojure
;; Example from source
(defn sieve
  [p xs]
  [(filter p xs) (remove p xs)])

(first (sieve even? (range 9)))
```

* __Sources and Excerpts:__

    -  **Source:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/)<br>
    **Excerpt:** “Using positional return values encodes meaning to indices, giving semantic or business meaning to indices/ordering. It's better to encode that meaning as explicit keywords”
