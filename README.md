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
;; Example from source

(when (not (empty? x)) ...)

(when-not (empty? x) ...)
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
;; Example from source

(into [] xs)

(into #{} xs)

(into {} (map (fn [[k v]] [k (f v)]) m))

(into {} (for [[k v] m] [k (f v)]))
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
