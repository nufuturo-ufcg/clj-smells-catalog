# Catalog of Clojure-related code smells


# Summary of the Catalog

This repository presents a catalog of code smells related to the Clojure ecosystem. It contains 35 Clojure-specific and 23 Functional-related smells. Each smell contains a description, code example, source and supporting excerpt from source indicating the smell. You can find the details on how we built this catalog in the [Methodology section](#methodology).

# Table of Contents
<!-- no toc -->
- [Clojure-specific Smells](#clojure-specific-smells)
  - [Unnecessary Macros](#unnecessary-macros)
  - [Immutability Violation](#immutability-violation)
  - [Namespaced Keys Neglect](#namespaced-keys-neglect)
  - [Improper Emptiness Check](#improper-emptiness-check)
  - [Map With Nil Values](#map-with-nil-values)
  - [Unnecessary `into`](#unnecessary-into)
  - [Conditional Build-Up](#conditional-build-up)
  - [Verbose Checks](#verbose-checks)
  - [Production `doall`](#production-doall)
  - [Redundant `do` block](#redundant-do-block)
  - [Thread Ignorance](#thread-ignorance)
  - [Nested Forms](#nested-forms)
  - [Direct usage of `clojure.lang.RT`](#direct-usage-of-clojurelangrt)
  - [Non-Idiomatic Record Construction](#non-idiomatic-record-construction)
  - [Misuse of Dynamic Scope](#misuse-of-dynamic-scope)
  - [Implicit Namespace Dependencies](#implicit-namespace-dependencies)
  - [Namespace Load Side Effects](#namespace-load-side-effects)
  - [Blocking Inside Go](#blocking-inside-go)
  - [Nested Atoms](#nested-atoms)
  - [Single-segment Namespace](#single-segment-namespace)
  - [Dynamic Scoped Singleton Resource](#dynamically-scoped-singleton-resource)
  - [Overengineering with `core.async`](#overengineering-with-coreasync)
  - [Excessive Refers](#excessive-refers)
  - [Unnecessary Laziness](#unnecessary-laziness)
  - [Relying on Load-Time Side Effects](#relying-on-load-time-side-effects)
  - [Monolithic Namespace Split](#monolithic-namespace-split)
  - [Unmanaged Resource I/O](#unmanaged-resource-io)
  - [Refs in Dependency Vector](#refs-in-dependency-vector)
  - [Misuse of Channel Closing Semantics](#misuse-of-channel-closing-semantics)
  - [Misused Threading](#misused-threading)
  - [Marker Protocol](#marker-protocol)
  - [Multiple Evaluation in Macros](#multiple-evaluation-in-macros)
  - [Case with Non-Literal Test Values](#case-with-non-literal-test-values)
  - [Non-Idiomatic Parameter Binding](#non-idiomatic-parameter-binding)
  - [Private Multimethods](#private-multimethods)
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
  - [Redundant Conditional Form](#redundant-conditional-form)
  - [Unmanaged Eager Realization](#unmanaged-eager-realization)
  - [Reinventing Dispatch](#reinventing-dispatch)
  - [The Heisenparameter](#the-heisenparameter)
  - [Inappropriate Use of Future](#inappropriate-use-of-future)
  - [Nil Arguments](#nil-arguments)
  - [Deeply Nested Conditional](#deeply-nested-conditional)
  - [Lazy Sequence Accumulation](#lazy-sequence-accumulation)
  - [Global Test Fixture Cache](#global-test-fixture-cache)
  - [Inline Complex Operation](#inline-complex-operation)
- [Methodology](#methodology)
  
# Clojure-specific Smells

## Unnecessary Macros

* __Description:__ This code smell occurs when macros are used in situations where simpler, more conventional solutions—such as functions or existing language constructs—would suffice. While macros offer powerful metaprogramming capabilities, their overuse introduces unnecessary abstraction and complexity. This can obscure the code’s intent, make debugging more challenging and reducing maintainability.

* __Example:__
```clojure
(defmacro log-and-exec [expr]
  `(do
     (println "Running...")
     ~expr))
```

* __Sources and Excerpts:__

  -  **Source:** [Forum - Structuring Clojure applications](https://news.ycombinator.com/item?id=34052268)<br>
      **Excerpt:** “Using macros when regular functions would do is a good example of that. It is absolutely possible to write impenetrable Clojure if you start doing weird things just because you can.”

## Immutability Violation

* __Description:__ This code smell occurs when mutable state is used in a language or paradigm that emphasizes immutability (such as Clojure), leading to side effects, reduced predictability, and harder-to-maintain code.

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


## Map With Nil Values

* __Description:__ This code smell occurs when `nil` values are inserted into a map. In Clojure, both a missing key and a key explicitly associated with `nil` return `nil` when accessed, making it difficult to distinguish between the two cases. This ambiguity can obscure program intent, lead to subtle bugs, and complicate reasoning about data state. Instead of inserting `nil`, prefer omitting the key entirely or using a sentinel value that more clearly expresses the intended meaning.

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

## Non-Idiomatic Record Construction

* __Description:__ This smell occurs when a developer uses the Java Interoperability positional constructor (e.g., `(->MyRecord val1 val2)`) to instantiate a `defrecord`. This method is non-idiomatic, relies on the positional order of fields, and causes code to break silently if a field is added or reordered in the record definition. The idiomatic Clojure alternative is to use the `map->RecordName` constructor with keyword arguments or the factory function with keyword arguments, which decouple instantiation from field order.

* __Example:__
```clojure
(defrecord Foo [a b])

(Foo. 1 2)
;;=> #user.Foo{:a 1, :b 2}
```

* __Sources and Excerpts:__

  -  **Source:** [Code File](https://github.com/corpix/clojure-koans/blob/372ba7d2deb6b78d3c6a2fa0a7ed0d0c279b19f7/src/koans/19_datatypes.clj)<br>
  -  **Source:** [Record Constructors](https://stuartsierra.com/2015/05/17/clojure-record-constructors/)<br>
      **Excerpt:** “defrecord and deftype compile into Java classes, so it is possible to construct them using Java interop syntax like this [...] But don't do that. Interop syntax is for interop with Java libraries.”

## Misuse of Dynamic Scope

* __Description:__ Occurs when dynamic variables (`def ^:dynamic`) and the binding macro are used without a compelling reason, such as for holding core application data or for passing information in asynchronous/multi-threaded contexts. This introduces hidden mutable state, makes the code hard to debug due to implicit dependencies, and should be reserved only for well-understood contextual configuration or explicit Dynamically Scoped Resources.

* __Example:__
```clojure
(def ^:dynamic *restarts* [])

(def ^:dynamic *restart-bindings* {})

(def ^:dynamic *call-stack* [])
```

* __Sources and Excerpts:__

  -  **Source:** [Code File](https://github.com/jimrthy/protektor/blob/7fec7073fe66207d3eb1a84d74851c5580f463cb/src/protektor/core.clj)<br>
      **Excerpt:** "All these dynamic globals are definite code smell. [...] I don't like them. At the same time...they're exactly what I need. Or so it seems."
  -  **Source:** [Issue](https://github.com/weavejester/codox/issues/202)<br>
      **Excerpt:** "In general, there's a very narrow set of circumstances where dynamic vars are a good idea."

## Implicit Namespace Dependencies

* __Description:__ This smell occurs when a developer relies on symbols from another namespace without explicitly declaring them (e.g., using `(:refer :all)` or `(:use ...)`). This practice introduces symbol ambiguity, leads to namespace pollution, and creates implicit dependencies that static analysis tools (linters, refactoring engines) cannot reliably resolve. This significantly reduces code clarity and increases the risk of name collisions.

* __Example:__
```clojure
(ns samples.web
  (:require [compojure.core :refer :all]
            [compojure.route :as route]))
(defroutes app
           (GET "/" [] "<h1>Hello World</h1>")
           (route/not-found "<h1>Page not found</h1>"))
```

* __Sources and Excerpts:__

  -  **Source:** [Issue](https://github.com/borkdude/grasp/issues/14)<br>
      **Excerpt:** "The :refer :all in clojure matches the symbol of GET with compojure.core/GET. but in grasp it does not match anything and defaults to the current namespace"

## Namespace Load Side Effects

* __Description:__ Performing operations such as `require` or `requiring-resolve` in a top-level form outside of the primary `ns` macro. This is a anti-pattern because it introduces hidden, dynamic dependencies that bypass the build tool's static dependency graph. This breaks predictable load ordering, leading to non-deterministic compilation, making the code harder to analyze, test, and maintain.

* __Example:__
```clojure
;; Not from source
(ns my-app.core)
(def some-var
  (requiring-resolve 'my-app.config/get-setting))
```

* __Sources and Excerpts:__

  -  **Source:** [Issue](https://github.com/metabase/metabase/issues/52004)<br>
      **Excerpt:** "Do not use `require` in a top-level form outside of `ns` [...]."

## Blocking Inside Go

* __Description:__ This code smell occurs when a blocking operation (`a/<!!`, `a/>!!`, or general blocking I/O) is called inside a `go` block. `go` blocks are designed for non-blocking, cooperative concurrency and execute on a small, fixed-size thread pool. Blocking inside a `go` block defeats this purpose, risking thread starvation, deadlocks, and system-wide performance degradation.

* __Example:__
```clojure
;; Not from source
(ns my-app.async-fail
  (:require [clojure.core.async :as a]))

(def my-chan (a/chan))

(a/go
  (println "Blocked thread consuming:" (a/<!! my-chan)))
```

* __Sources and Excerpts:__

  -  **Source:** [Issue](https://github.com/replikativ/datahike/issues/303)<br>
      **Excerpt:** "This is a call to >!! or <!! inside a go block causing this, which effectively blocks an internal go dispatch thread, so clearly bad practice from whatever is doing that [...]."

## Nested Atoms

* __Description:__ Storing an `Atom` or other managed reference (like a `Volatile` or `Ref`) inside another `Atom`. This is an anti-pattern because it violates the principle of atomic state management. Updating the inner `Atom` does not update the outer `Atom`'s value, making it impossible to guarantee a single, consistent snapshot of the overall state at any time. This might lead to complicated state transitions and undermine the simplicity of the state model.

* __Example:__
```clojure
;; Not from source
(def global-state
  (atom {:ui-state  {:theme :light}
         :history (atom [])}))
```

* __Sources and Excerpts:__

  -  **Source:** [Issue](https://github.com/andrewleverette/clojulator/issues/6)<br>
      **Excerpt:** "[...] managing the UI state led to including that history atom in the global state atom. Nested atoms seem to be an anti-pattern."

## Single-segment Namespace

* __Description:__ This structural anti-pattern occurs when a project uses single-segment namespaces (e.g., `digest` or `config` instead of `my-app.digest` or `my-app.config`). This practice violates the standard, hierarchical naming convention of the Clojure ecosystem, causing issues: it increases the risk of global naming collisions, reduces the clarity of code organization, and frequently leads to tooling errors that rely on predictable, qualified names.

* __Example:__
```clojure
;; Not from source
(ns digest) 
(defn md5 [s] ...)
```

* __Sources and Excerpts:__

  -  **Source:** [Issue](https://github.com/clj-easy/graal-build-time/issues/35)<br>
      **Excerpt:** "As single-segment namespaces are an anti-pattern in Clojure, I'm happy not to invest any time in finding a way to make them work."

## Dynamically-Scoped Singleton Resource

* __Description:__ The use of Dynamic Variables (`def ^:dynamic`) and the `binding` macro to implicitly manage and pass critical, transactional resources (such as database connections, active transactions, or thread pools). This anti-pattern prevents thread dispatch, breaks lazy sequences, limits the application to one resource per thread, and creates External Data Coupling by hiding dependencies in implicit, thread-local state instead of passing them as explicit function arguments. This greatly increases the risk of silent transactional failure and debugging difficulty.

* __Example:__
```clojure
;; Example from source
(ns com.example.library)

(def ^:dynamic *resource*)

(defn- internal-procedure []
  ;; ... uses *resource* ...
  )

(defn public-api-function [arg]
  ;; ... calls internal-procedure ...
  )
```

* __Sources and Excerpts:__

  -  **Source:** [On the Perils of Dynamic Scope](https://stuartsierra.com/2013/03/29/perils-of-dynamic-scope/)<br>
      **Excerpt:** "The problem with this pattern, especially in libraries, is the constraints it imposes on any code that wants to use the library."
  -  **Source:** [Issue](https://github.com/steffan-westcott/clj-otel/issues/2)<br>
      **Excerpt:** "I should also point out that I am unsure of the merits of dynamic scoped objects."

## Overengineering with `core.async`

* __Description:__ The misuse of the full abstraction of the `clojure.core.async` library for simple asynchronous tasks, such as returning a result that only involves a single value or a one-time response. Channels are designed for complex streams of events or coordinating multiple concurrent processes. Using channels for simple tasks introduces unnecessary complexity, increases cognitive load, and adds overhead when simpler, more expressive abstractions like Promises/Deferreds (e.g., `promesa`) or direct callbacks are sufficient and more idiomatic.

* __Example:__
```clojure
;; Not from source
(ns my-app.async-smell
  (:require [clojure.core.async :as a]))

(defn fetch-single-result-smelly [url]
  (let [ch (a/chan)]
    (a/go (a/>! ch (http/get url)))
    ch))
```

* __Sources and Excerpts:__

  -  **Source:** [Issue](https://github.com/oliyh/re-graph/issues/11)<br>
      **Excerpt:** "Regarding core-async in general I've always found it to be an anti-pattern to use it for channels that only ever return one value, I think callback-fns or promises are better in these instances."

## Excessive Refers

* __Description:__ Occurs when a namespace explicitly `refer`s a large number of Vars (or uses the anti-pattern `(:refer :all)`) from another namespace. This practice leads to Namespace Pollution, drastically increases the risk of name collisions with other libraries or future code, and makes the source of any function call ambiguous.

* __Example:__
```clojure
;; Not from source
(ns my-app.core
  (:require [my-lib.utils
             :refer [this
                     that
                     the
                     other
                     more
                     moar]])) ; <--- Long list of referred symbols
```

* __Sources and Excerpts:__

  -  **Source:** [Issue](https://github.com/clojure-emacs/refactor-nrepl/issues/305)<br>
      **Excerpt:** "Having tons of referred symbols is an anti-pattern anyway, so we should nudge people toward not doing that."
  -  **Source:** [Issue](https://github.com/clj-kondo/clj-kondo/issues/342)<br>
      **Excerpt:** "Clojure style guide recommends `:as` or `:refer [...]` over `:refer :all`"

## Unnecessary Laziness

* __Description:__ The default use of lazy sequence functions (e.g., `map`, `filter`) when an eager sequence function (e.g., `mapv`, `into []`) would be more efficient, less complex, and better communicate the developer's intent. Using lazy sequences without a specific need (like infinite length or controlled side effects) adds complexity, risks unexpected realization bugs, and contributes to the Lazy Side Effects smell by making performance unpredictable.

* __Example:__
```clojure
;; Not from source
(defn process-smelly [coll]
  (let [doubled (map #(* 2 %) coll)]
    (vec doubled))) ; Forces realization later
```

* __Sources and Excerpts:__

  -  **Source:** [Issue](https://github.com/taoensso/faraday/issues/99)<br>
      **Excerpt:** "I.e. would suggest that using laziness when one doesn't specifically need/want laziness is an anti-pattern."

## Relying on Load-Time Side Effects

* __Description:__ The practice of relying on a function or macro's output being static or globally available because it was evaluated as a top-level form during namespace loading. This is a anti-pattern because the code relies on the unmanaged side effect of the loading process and the mutable state of the running system. This fragility breaks static analysis, causes non-deterministic behavior during REPL reloading, and violates the functional contract that code should be safe regardless of execution order.

* __Example:__
```clojure
;; Not from source
(ns my-app.fragile-config)
;; Relies on this complex function running *only once* at load time
(def CONFIG (calculate-heavy-config (some/global-atom)))
```

* __Sources and Excerpts:__

  -  **Source:** [Issue](https://github.com/clj-commons/kibit/issues/14)<br>
      **Excerpt:** "Relying on load time behavior for non-declarative operations is a huge antipattern."

## Monolithic Namespace Split

* __Description:__ The practice of splitting a single logical namespace across multiple physical files using the legacy, imperative macros `load` and `in-ns`. It breaks static analysis and build tools on dependency resolution, leading to fragile code that is difficult to manage. This smell should be replaced by creating separate, distinct namespaces and managing them explicitly with require.

* __Example:__
```clojure
;; Example from source
(ns slamhound-test.core)
(load "core_extra.clj")
(defn -main [& args]
  (pprint args)
  (io/copy (ByteArrayInputStream. (.getBytes "hello"))
           (first args)))

(in-ns 'slamhound-test.core)
(defn temp [])
```

* __Sources and Excerpts:__

  -  **Source:** [Issue](https://github.com/technomancy/slamhound/issues/61)<br>
      **Excerpt:** "It's worth noting that the clojure.core namespace is very atypical for bootstrapping reasons and should not be considered an example of good style."

## Unmanaged Resource I/O

* __Description:__ The failure to use the `with-open` macro when dealing with resources that implement `java.io.Closeable` (e.g., `Reader`, `Writer`, sockets, or streams). This omission leads to resource leaks  and potential system instability by preventing the resource from being released back to the operating system. This violates the principle of managed side effects in I/O operations and must be fixed with the explicit use of `with-open` to guarantee cleanup.

* __Example:__
```clojure
;; Not from source
(defn read-file-smelly [filename]
  (let [reader (io/reader filename)]
    (line-seq reader)))
```

* __Sources and Excerpts:__

  -  **Source:** [Pull Request](https://github.com/metabase/metabase/pull/59728)<br>
      **Excerpt:** "Huge no-no to open up a java.io.Reader and not close it (use with-open here)."

## Refs in Dependency Vector

* __Description:__ The anti-pattern of placing a mutable state reference object (an Atom, a `use-state` object, or a raw `use-ref` object) directly into a hook's dependency array (`[]`). Dependency arrays rely on comparing values for change detection. Placing the reference object often causes the hook to either never re-run (if the reference is stable) or re-run unexpectedly (if the framework updates the reference). The idiomatic solution is to track the dereferenced value (`@ref`) instead.

* __Example:__
```clojure
;; Not from source
(def my-atom-ref (r/atom 0))

(defn MyComponent []
  (r/use-effect
    (fn []
      (println "Value is now:" @my-atom-ref))
    [my-atom-ref])) 
```

* __Sources and Excerpts:__

  -  **Source:** [Pull Request](https://github.com/penpot/penpot/pull/5658)<br>
      **Excerpt:** "having the state reference object on deps and derefing on use-effect has no real meaning and is an anti pattern in any way"

## Misuse of Channel Closing Semantics

* __Description:__ This smell occurs when a channel-based data stream uses a custom sentinel value (e.g., `:done`, `:EOF`, `::end`) to signal termination, rather than relying on the standard `core.async` semantic contract. This contract dictates that a stream is terminated by closing the channel with `a/close!`, which causes subsequent reads (`a/<!`) to return `nil`. Violating this semantic coupling introduces brittle inspection logic (`(when (not= :done event) ...)`) and breaks the idiomatic flow control pattern of `when-let` and `loop/recur`.

* __Example:__
```clojure
;; Not from source
(def my-chan (a/chan 1))
(a/put! my-chan :done)

(a/go
  (loop []
    (when-let [event (a/<! my-chan)]
      (when (not= event :done) ; <-- Brittle sentinel check
        (prn "Processing" event)
        (recur)))))
```

* __Sources and Excerpts:__

  -  **Source:** [Pull Request](https://github.com/wkok/openai-clojure/pull/63)<br>
      **Excerpt:** "I do think that stopping on :done rather than a channel closing is non-idiomatic and bad practice."

## Misused Threading

* __Description:__ The misuse of threading macros (`->` or `->>`) to chain together operations where the data type of the threaded argument changes fundamentally at each step (e.g., threading a map into a string, into a `File` object, and back into a map). Threading macros are intended for homogeneous, sequential transformations on a similar data type.

* __Example:__
```clojure
;; Example from source
(defn read-project-raw [project]
  (-> project
      (:root)
      (io/file "project.clj")
      (str)
      (project/read-raw)))
```

* __Sources and Excerpts:__

  -  **Source:** [Pull Request](https://github.com/amperity/lein-monolith/pull/97)<br>
      **Excerpt:** "Also, stylistically, I think this is an example of a threading antipattern - thread-first and thread-last are best used to chain together successive transformations on a similar argument type."

## Marker Protocol

* __Description:__ The use of `defprotocol` solely to define a type identifier (a "marker") for use with type checking, rather than defining a contract for polymorphic behavior. The `defprotocol` macro is intended for defining methods that can be extended to different types. Making it a marker introduces the complexity and overhead of the protocol machinery unnecessarily.

* __Example:__
```clojure
;; Example from source
  clojure.lang.IEquiv
  (-equiv [_ other]
    (and (satisfies? IUUID other)
         (identical? uuid (.-uuid other))))
```

* __Sources and Excerpts:__

  -  **Source:** [Pull Request](https://github.com/Tensegritics/ClojureDart/pull/262)<br>
      **Excerpt:** "Marker protocols are generally a code smell to me."

## Multiple Evaluation in Macros

* __Description:__ This smell occurs when a macro inserts one of its input argument forms (which can be an arbitrary expression) into the generated code more than once without first binding it to a local, temporary variable (e.g., using a gensym like let `[value# ~value]`). This is a violation of macro hygiene and leads to Hidden Side Effects: the argument expression is unintentionally evaluated multiple times, causing performance degradation or triggering unwanted side effects for the caller.

* __Example:__
```clojure
;; Not from source
(def counter (atom 0))

(defmacro double-log [x]
  `(do (println "Value:" ~x) (swap! counter inc) (println "Value:" ~x)))

(double-log (swap! counter inc)) 
```

* __Sources and Excerpts:__

  -  **Source:** [Pull Request](https://github.com/clj-commons/manifold/pull/225)<br>
      **Excerpt:** "Had you inserted value more than once, it could be evaluated multiple times, which you rarely want."

## Case with Non-Literal Test Values

* __Description:__ This smell occurs when the `case` macro is used with test expressions that rely on runtime values (such as bindings or variables) instead of compile-time constants (literals). The `case` macro is optimized to test for the identity of literals and does not guarantee correct runtime equality (`=`) for dynamic values. Using it with non-literal values is a dangerous practice that can lead to subtle, difficult-to-debug logic errors. Developers must use `cond` or `condp` for all runtime comparison logic.

* __Example:__
```clojure
;; Not from source
(defn MyCameraView [current-orientation]
  (case result
    "portrait"  (do-portrait-logic)
    "landscape" (do-landscape-logic)
    :else       (do-default-logic)))
```

* __Sources and Excerpts:__

  -  **Source:** [Pull Request](https://github.com/status-im/status-mobile/pull/16781)<br>
      **Excerpt:** "`case` should be used only with literals"

## Non-Idiomatic Parameter Binding

* __Description:__ The use of confusing or non-standard syntax (such as `& [x]`) to define a single optional argument or variadic arity. This binding method is verbose, structurally confusing (as it captures a single argument from a sequence), and obscures the function's contract. This must be refactored by replacing the complex binding with the explicit, idiomatic practice of using multiple function arities or a clean options map.

* __Example:__
```clojure
;; Not from source
(defn- CustomExtension-make [project & [ns]]
  (if ns
    (do-stuff project ns)
    (do-stuff project nil)))
```

* __Sources and Excerpts:__

  -  **Source:** [Pull Request](https://github.com/weavejester/codox/pull/205)<br>
      **Excerpt:** "`& [ns]` is something we should replace with proper argument arities (or omitted entirely, if they are never used)"

## Private Multimethods

* __Description:__ The use of a private function (`defn-`) or metadata to define a `defmulti` or its `defmethods`. Multimethods are designed for dynamic, open polymorphism and external extension. Making them private defeats their architectural purpose, forces the abstraction to behave as a closed system, and introduces unnecessary complexity and overhead.

* __Example:__
```clojure
;; Not from source
(defn- ^:private process-event [event]
  (defmulti process-event :type))

(defmethod process-event :user/create [{:keys [user]}]
  (log/info "Creating user" user))
```

* __Sources and Excerpts:__

  -  **Source:** [Commit Message](https://github.com/greglook/cljstyle/commit/a52ff57f52670ef2866d3a574557ef9c5198805e)<br>
      **Excerpt:** "Private multimethods are a code smell."

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
    **Excerpt:** “Although mapping a side-effect function over a sequence is almost certainly an anti-pattern, sometimes there are use cases for it. However, laziness in such cases might make you scratch your head for hours until you realize why the side effect never happened.”

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
    **Excerpt:** “Using explicit recursion tends to be a code smell, there's a good chance that there's a higher order function that can do the job”

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

## Redundant Conditional Form

* __Description:__ This smell occurs when a developer uses the general-purpose `cond` macro with repetitive patterns where a simpler, more expressive, and specialized form like `condp` or `case` is available. This repetition (e.g., checking against the same value with the same predicate in every branch) introduces unnecessary verbosity and reduces code clarity, effectively `Reinventing the Wheel` of specialized conditional dispatch.

* __Example:__
```clojure
;; Example from source
(cond
    (= 1 x) :one
    (= 2 x) :two
    (= 3 x) :three
    (= 4 x) :four)
```

* __Sources and Excerpts:__

    -  **Source:** [Code File](https://github.com/NoahTheDuke/splint/blob/5d5278fa9fbaaecabecb19e70e246b2ded773936/src/noahtheduke/splint/rules/style/prefer_condp.clj)<br>
    **Excerpt:** “`cond` checking against the same value in every branch is a code smell.”

## Unmanaged Eager Realization

* __Description:__ This smell occurs when a function, often related to I/O or stream processing, eagerly and implicitly buffers an entire data source into memory (like a file or a media stream) instead of processing it sequentially or lazily. This leads to unmanaged resource consumption and risks `OutOfMemoryError` exceptions , significantly hindering scalability and violating functional programming's principle of controlled resource management.

* __Example:__
```clojure
;; Example from source
(defn ?->InputStream
  [bindata]
  (cond
    (satisfies? Media bindata)
    (.getInputStream bindata)

    (satisfies? ChunkedStream bindata)
    (.getInputStream (ChunkedStream->Media bindata))))
```

* __Sources and Excerpts:__

    -  **Source:** [Code File](https://github.com/Stevenlimo/anvil-runtime/blob/149eadf044e0a847a2c1e88f3e96ea5599dfea92/server/core/src/anvil/dispatcher/types.clj)<br>
    **Excerpt:** “Turn ChunkedStream or Media into an InputStream. Use of this function is a code smell,
   because it causes the whole Media to be buffered in memory at the same time. We should
   invent ways not to have to use it.”

## Reinventing Dispatch

* __Description:__ Manual implementation of state/action dispatch using complex nested `cond` or `case` structures instead of idiomatic, specialized abstractions (e.g., Multimethods, Protocols, or Function Maps). This introduces unnecessary complexity, reduces readability, and makes the code difficult to extend and maintain as new states or types are added. 

* __Example:__
```clojure
;; Not from source
(defn run-pda
  [[state clicked]]
  (cond
    (and (= state :start) (= clicked :coin)) :running
    (and (= state :running) (= clicked :stop)) :stopped
    (and (= state :running) (not= clicked :stop)) :running
    :else (throw (ex-info "Invalid transition"))))
```

* __Sources and Excerpts:__

    -  **Source:** [Issue](https://github.com/ivansalazar/qwircl/issues/8)<br>
    **Excerpt:** “It seems that at least run-pda is only dispatching on [state clicked]. This seems to be a suggestion that multimethods are better suited to this task.”

## The Heisenparameter

* __Description:__ This smell occurs when a function parameter is designed to accept multiple, fundamentally different types (e.g., a single value, a `list`, or a `set`) to represent the same concept. This practice is often done for "convenience" but it sacrifices clarity and predictability (hence "Heisenparameter"). It forces the function's internal logic to use complex type-checking and conditional branching to normalize the input, making the function hard to reason about, test, and maintain.

* __Example:__
```clojure
;; Example from source
(defn wrap-coll
  "Wraps argument in a vector if it is not already a collection."
  [arg]
  (if (coll? arg)
    arg
    [arg]))

(defn process
  "Processes a single input or a collection of inputs."
  [input]
  (process-batch (wrap-coll input)))
```

* __Sources and Excerpts:__

    -  **Source:** [Issue](https://github.com/pmonks/multigrep/issues/5)<br>
    **Excerpt:** “the use of function parameters that may either be collections or singletons is an anti-pattern”
    -  **Source:** [The Heisenparameter](https://stuartsierra.com/2015/06/10/clojure-donts-heisenparameter/)<br>
    **Excerpt:** “A pattern I particularly dislike: Function parameters which may or may not be collections.”

## Inappropriate Use of Future

* __Description:__ Using `future` as a general-purpose asynchronous primitive, which reduces control over execution, complicates error handling, and introduces hidden concurrency issues (unbounded thread creation, resource exhaustion) compared to dedicated, managed libraries (e.g., `core.async`, `Promesa`, `Manifold`). future should generally be replaced by mechanisms that use managed thread pools or cooperative scheduling.

* __Example:__
```clojure
(continue [c f] (future (f @c)))
```

* __Sources and Excerpts:__

    -  **Source:** [Issue](https://github.com/metosin/sieppari/issues/9)<br>
    **Excerpt:** “To do this, you definitely shouldn't use future. It potentially creates a new thread per invocation. The threads are reused, but there is no maximum.”

## Nil Arguments

* __Description:__ Occurs when a function's API requires callers to pass explicit primitive placeholders (most commonly `nil`, but also `0` or empty strings) for optional parameters that are not being used. This violates the principle of idiomatic function design, increases boilerplate, and sacrifices expressiveness for minimal gain in API strictness or backward compatibility. This practice is often a vestige of Java interop or poor function arity design.

* __Example:__
```clojure
;; Not from source
(defn execute-smelly [query options success-cb error-cb]
  ;; ... function body ...
  )
(execute-smelly :find-users {} nil nil) 
```

* __Sources and Excerpts:__

    -  **Source:** [Issue](https://github.com/walmartlabs/lacinia/issues/316)<br>
    **Excerpt:** “Passing nils seem like such a code-smell.”

## Deeply Nested Conditional

* __Description:__ This structural flaw occurs when complex control flow is managed through excessive vertical nesting of conditional forms (such as nested `if` or `when` statements). This practice severely increases the cognitive load required to track the program's state and branching logic. This smell should be refactored by replacing the nesting with the idiomatic "guard clause" pattern, typically using sequential `when` or `when-not` forms that enforce early exits via throw or a return value, thus flattening the control flow.

* __Example:__
```clojure
;; Example from source
(if looks-encrypted
  (if (encryption/default-encryption-enabled?)
    (if (string/valid-uuid? (encryption/maybe-decrypt raw))
      (log/debug "Database encrypted and MB_ENCRYPTION_SECRET_KEY correctly configured")
      (throw (Exception. "Database was encrypted with a different key than the MB_ENCRYPTION_SECRET_KEY environment contains")))
    (throw (Exception. "Database is encrypted but the MB_ENCRYPTION_SECRET_KEY environment variable was NOT set")))
  (if (encryption/default-encryption-enabled?)
    (do
      (log/info "New MB_ENCRYPTION_SECRET_KEY environment variable set. Encrypting database...")
      (mdb.encryption/encrypt-db db-type data-source nil)
      (log/info "Database encrypted..." (u/emoji "✅")))
    (log/debug "Database not encrypted and MB_ENCRYPTION_SECRET_KEY env variable not set.")))
```

* __Sources and Excerpts:__

    -  **Source:** [Pull Request](https://github.com/metabase/metabase/pull/51546)<br>
    **Excerpt:** “Usually nested ifs like this is kind-of a code smell.”

## Lazy Sequence Accumulation

* __Description:__ This smell occurs when a developer uses lazy sequence functions (like `concat`, `map`, or `filter`) within a non-lazy, eager evaluation context (such as `reduce` or `apply merge-with`). The code attempts to build up a large, complex lazy result inside a sequential loop, leading to massive memory consumption, performance issues, and often causes a `StackOverflowError` when the interpreter attempts to eagerly realize the deeply nested sequence structure.

* __Example:__
```clojure
;; Example from source
(first (:a (apply merge-with concat
                  (map (fn [n] {:a (range 1 n)})
                       (range 1 4000)))))
```

* __Sources and Excerpts:__

    -  **Source:** [Clojure Don'ts: Concat](https://stuartsierra.com/2015/04/26/clojure-donts-concat/)<br>
    **Excerpt:** “Don't use lazy sequence operations in a non-lazy loop.”

## Global Test Fixture Cache

* __Description:__ This smell occurs when `clojure.test/use-fixtures` (especially `:once` fixtures) are misused to load and maintain mutable global state (like database connections, shared components, or stateful Atoms) across multiple tests. This anti-pattern destroys test isolation, leading to non-deterministic test runs where one test's side effects corrupt the state for subsequent tests . This violates the core principle of testing: that every test must be able to run independently. Fixtures should only be used to cache expensive, immutable resources.

* __Example:__
```clojure
;; Not from source
(defonce *database-conn* (atom nil))

(use-fixtures :once
  (fn [f]
    (reset! *database-conn* (setup-db-connection))
    (f)
    (close-db-connection)))

(deftest test-user-creation
  ;; This test relies on the mutable state left by prior tests
  (is (not (nil? @*database-conn*)))
  (db/insert @*database-conn* {:user "Alice"}))
```

* __Sources and Excerpts:__

    -  **Source:** [Pull Request](https://github.com/fluree/db/pull/244)<br>
    **Excerpt:** “I didn't mean to imply that we should never use `use-fixtures`, but that we should just use it sparingly.”
    -  **Source:** [Fixtures as Caches](https://stuartsierra.com/2016/05/19/fixtures-as-caches/)<br>
    **Excerpt:** “But if you want true isolation between your tests then they should not share any state at all. The only reason for sharing fixtures across tests is when the fixture does something expensive or time-consuming.”


## Inline Complex Operation

* __Description:__ This smell occurs when a small, typically inline block of code contains or generates complex, multi-step business logic instead of delegating that work to a named, pure function. This failure to factor code  leads to code generation bloat, hinders optimization efforts by the compiler, and severely reduces the readability and testability of the logic block.

* __Example:__
```clojure
;; Not from source
(defrule calculate-discount
  [:user :id ?user-id :status "VIP"]
  =>
  (let [base-price (get-in @db/store [:items ?item-id :price])
        discount   (* base-price 0.15)
        new-price  (- base-price discount)]
    (insert! (->Purchase ?user-id ?item-id new-price))))
```

* __Sources and Excerpts:__

    -  **Source:** [Issue](https://github.com/oracle-samples/clara-rules/issues/383)<br>
    **Excerpt:** “Doing a bunch of (crazy) operations inline should be considered an anti-pattern. It also makes it more difficult to do optimizations like done here.”

# Methodology

The methodology follows the [original study](https://doi.org/10.1007/s10664-023-10343-6) on Elixir by Vegi & Valente (2023), with adaptations for the specifics of Clojure.

The figure below summarizes the overall research process:

<p align="center">
  <img 
    src="https://github.com/user-attachments/assets/8c431516-f502-4176-a202-27b8b4f3a492"
    width="50%"
  />
</p>

<br/>

In a nutshell, our study began with a structured Google search using keywords related to Clojure and code smells to locate relevant community discussions. From this search, we identified forums, blogs, and other practitioner-oriented sources where we analyzed reports and debates to uncover recurring problematic patterns frequently mentioned by experienced Clojure developers. Based on this initial analysis, we compiled the first version of the catalog, grounded in real-world experiences shared across various online platforms.

After building this initial catalog, we shared it through the main communication channels of the Clojure community to gather feedback, validate the relevance of the identified smells, and understand how practitioners perceive them in practice.

In the next phase, we expanded the study by mining repositories from the Clojure ecosystem on GitHub. We analyzed issues, pull requests, commits, and code files to identify new code smells. This second round of analysis allowed us to expand the catalog and strengthen its empirical foundation.

Contributions are welcome via Issues and Pull Requests.
