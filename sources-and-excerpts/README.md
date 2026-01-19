# Sources and Excerpts

# Table of Sources
<!-- no toc -->
- [Clojure-specific Smells](#clojure-specific-smells)
  - [Unnecessary Macros](#unnecessary-macros)
  - [Immutability Violation](#immutability-violation)
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
  
# Clojure-specific Smells

## Unnecessary Macros

  -  **Source:** [Forum - Structuring Clojure applications](https://news.ycombinator.com/item?id=34052268)<br>
      **Excerpt:** “Using macros when regular functions would do is a good example of that. It is absolutely possible to write impenetrable Clojure if you start doing weird things just because you can.”

## Immutability Violation

  -  **Source:** [Forum - How to refactor a Java singleton to Clojure?](https://softwareengineering.stackexchange.com/questions/219780/how-to-refactor-a-java-singleton-to-clojure)<br>
      **Excerpt:** “Mutable state totally destroys this concept, and with it, the advantages of pure code. Clojure doesn't force you to be pure, but it certainly makes it easy to do so”

## Improper Emptiness Check

  -  **Source:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/)<br>
      **Excerpt:** “Don't use (not (empty? x))!”

## Map With Nil Values

  -  **Source:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/)<br>
      **Excerpt:** “Clojure maps are collections, not slots. Combined with nil's meaning being "nothing", nil values inside maps are confusing. [...] Try to avoid inserting nil values into a map.”

## Unnecessary `into`

  -  **Source:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/)<br>
      **Excerpt:** “`into` is a pretty useful function, but one often abused. The (mis)usage of into can usually be broken to three distinct cases: Type Transformation, Map Mapping and Not Using the Transducer API.”

## Conditional Build-Up

  -  **Source:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/)<br>
      **Excerpt:** “Conditional Build-Up”

## Verbose Checks

  -  **Source:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/)<br>
      **Excerpt:** Numbers! and Truth Be Told sections

## Production `doall`

  -  **Source:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/)<br>
      **Excerpt:** “`doall` is a macro which forcefully realizes lazy sequences. It should not be used in production.”

## Redundant `do` block

  -  **Source:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/)<br>
      **Excerpt:** “Some expressions have implicit `do` blocks in them, making it unnecessary to use a `do` block.”

## Thread Ignorance

  -  **Source:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/)<br>
      **Excerpt:** “Avoid trivial threading [...] And remember to thread with style.”

## Nested Forms

  -  **Source:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/)<br>
      **Excerpt:** “Plenty of macros with binding forms don't need to be nested.”

## Direct usage of `clojure.lang.RT`

  -  **Source:** [Forum - Is interop with clojure.lang.RT an anti-pattern in clojure? / consider adding iter to clojure.core](https://ask.clojure.org/index.php/10303/interop-clojure-pattern-clojure-consider-adding-iter-clojure)<br>
      **Excerpt:** “RT should be considered internal implementation and should not be called directly. Iterators, in general, are very un-clojurey. They are stateful and generally not concurrency friendly.”

## Non-Idiomatic Record Construction

  -  **Source:** [Code File](https://github.com/corpix/clojure-koans/blob/372ba7d2deb6b78d3c6a2fa0a7ed0d0c279b19f7/src/koans/19_datatypes.clj)<br>
  -  **Source:** [Record Constructors](https://stuartsierra.com/2015/05/17/clojure-record-constructors/)<br>
      **Excerpt:** “defrecord and deftype compile into Java classes, so it is possible to construct them using Java interop syntax like this [...] But don't do that. Interop syntax is for interop with Java libraries.”

## Misuse of Dynamic Scope

  -  **Source:** [Code File](https://github.com/jimrthy/protektor/blob/7fec7073fe66207d3eb1a84d74851c5580f463cb/src/protektor/core.clj)<br>
      **Excerpt:** "All these dynamic globals are definite code smell. [...] I don't like them. At the same time...they're exactly what I need. Or so it seems."
  -  **Source:** [Issue](https://github.com/weavejester/codox/issues/202)<br>
      **Excerpt:** "In general, there's a very narrow set of circumstances where dynamic vars are a good idea."

## Implicit Namespace Dependencies

  -  **Source:** [Issue](https://github.com/borkdude/grasp/issues/14)<br>
      **Excerpt:** "The :refer :all in clojure matches the symbol of GET with compojure.core/GET. but in grasp it does not match anything and defaults to the current namespace"

## Namespace Load Side Effects

  -  **Source:** [Issue](https://github.com/metabase/metabase/issues/52004)<br>
      **Excerpt:** "Do not use `require` in a top-level form outside of `ns` [...]."

## Blocking Inside Go

  -  **Source:** [Issue](https://github.com/replikativ/datahike/issues/303)<br>
      **Excerpt:** "This is a call to >!! or <!! inside a go block causing this, which effectively blocks an internal go dispatch thread, so clearly bad practice from whatever is doing that [...]."

## Nested Atoms

  -  **Source:** [Issue](https://github.com/andrewleverette/clojulator/issues/6)<br>
      **Excerpt:** "[...] managing the UI state led to including that history atom in the global state atom. Nested atoms seem to be an anti-pattern."

## Single-segment Namespace

  -  **Source:** [Issue](https://github.com/clj-easy/graal-build-time/issues/35)<br>
      **Excerpt:** "As single-segment namespaces are an anti-pattern in Clojure, I'm happy not to invest any time in finding a way to make them work."

## Dynamically-Scoped Singleton Resource

  -  **Source:** [On the Perils of Dynamic Scope](https://stuartsierra.com/2013/03/29/perils-of-dynamic-scope/)<br>
      **Excerpt:** "The problem with this pattern, especially in libraries, is the constraints it imposes on any code that wants to use the library."
  -  **Source:** [Issue](https://github.com/steffan-westcott/clj-otel/issues/2)<br>
      **Excerpt:** "I should also point out that I am unsure of the merits of dynamic scoped objects."

## Overengineering with `core.async`

  -  **Source:** [Issue](https://github.com/oliyh/re-graph/issues/11)<br>
      **Excerpt:** "Regarding core-async in general I've always found it to be an anti-pattern to use it for channels that only ever return one value, I think callback-fns or promises are better in these instances."

## Excessive Refers

  -  **Source:** [Issue](https://github.com/clojure-emacs/refactor-nrepl/issues/305)<br>
      **Excerpt:** "Having tons of referred symbols is an anti-pattern anyway, so we should nudge people toward not doing that."
  -  **Source:** [Issue](https://github.com/clj-kondo/clj-kondo/issues/342)<br>
      **Excerpt:** "Clojure style guide recommends `:as` or `:refer [...]` over `:refer :all`"

## Unnecessary Laziness

  -  **Source:** [Issue](https://github.com/taoensso/faraday/issues/99)<br>
      **Excerpt:** "I.e. would suggest that using laziness when one doesn't specifically need/want laziness is an anti-pattern."

## Relying on Load-Time Side Effects

  -  **Source:** [Issue](https://github.com/clj-commons/kibit/issues/14)<br>
      **Excerpt:** "Relying on load time behavior for non-declarative operations is a huge antipattern."

## Monolithic Namespace Split

  -  **Source:** [Issue](https://github.com/technomancy/slamhound/issues/61)<br>
      **Excerpt:** "It's worth noting that the clojure.core namespace is very atypical for bootstrapping reasons and should not be considered an example of good style."

## Unmanaged Resource I/O

  -  **Source:** [Pull Request](https://github.com/metabase/metabase/pull/59728)<br>
      **Excerpt:** "Huge no-no to open up a java.io.Reader and not close it (use with-open here)."

## Refs in Dependency Vector

  -  **Source:** [Pull Request](https://github.com/penpot/penpot/pull/5658)<br>
      **Excerpt:** "having the state reference object on deps and derefing on use-effect has no real meaning and is an anti pattern in any way"

## Misuse of Channel Closing Semantics

  -  **Source:** [Pull Request](https://github.com/wkok/openai-clojure/pull/63)<br>
      **Excerpt:** "I do think that stopping on :done rather than a channel closing is non-idiomatic and bad practice."

## Misused Threading

  -  **Source:** [Pull Request](https://github.com/amperity/lein-monolith/pull/97)<br>
      **Excerpt:** "Also, stylistically, I think this is an example of a threading antipattern - thread-first and thread-last are best used to chain together successive transformations on a similar argument type."

## Marker Protocol

  -  **Source:** [Pull Request](https://github.com/Tensegritics/ClojureDart/pull/262)<br>
      **Excerpt:** "Marker protocols are generally a code smell to me."

## Multiple Evaluation in Macros

  -  **Source:** [Pull Request](https://github.com/clj-commons/manifold/pull/225)<br>
      **Excerpt:** "Had you inserted value more than once, it could be evaluated multiple times, which you rarely want."

## Case with Non-Literal Test Values

  -  **Source:** [Pull Request](https://github.com/status-im/status-mobile/pull/16781)<br>
      **Excerpt:** "`case` should be used only with literals"

## Non-Idiomatic Parameter Binding

  -  **Source:** [Pull Request](https://github.com/weavejester/codox/pull/205)<br>
      **Excerpt:** "`& [ns]` is something we should replace with proper argument arities (or omitted entirely, if they are never used)"

## Private Multimethods

  -  **Source:** [Commit Message](https://github.com/greglook/cljstyle/commit/a52ff57f52670ef2866d3a574557ef9c5198805e)<br>
      **Excerpt:** "Private multimethods are a code smell."
