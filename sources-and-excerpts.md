# Sources and Excerpts

## Table of Contents
<!-- no toc -->
- [State & Concurrency](#state--concurrency)
  - [Immutability Violation](#immutability-violation)
  - [Blocking Inside Go](#blocking-inside-go)
  - [Nested Atoms](#nested-atoms)
  - [Misuse of Dynamic Scope](#misuse-of-dynamic-scope)
  - [Overengineering with `core.async`](#overengineering-with-coreasync)
  - [Dynamic Scoped Singleton Resource](#dynamically-scoped-singleton-resource)
  - [Unnecessary Laziness](#unnecessary-laziness)
    
- [Environment & Idioms](#environment--idioms)
  - [Unmanaged Resource I/O](#unmanaged-resource-io)
  - [Relying on Load-Time Side Effects](#relying-on-load-time-side-effects)
  - [Namespace Load Side Effects](#namespace-load-side-effects)
  - [Multiple Evaluation in Macros](#multiple-evaluation-in-macros)
  - [Direct usage of `clojure.lang.RT`](#direct-usage-of-clojurelangrt)
  - [Unnecessary Macros](#unnecessary-macros)
  - [Production `doall`](#production-doall)
  - [Misuse of Channel Closing Semantics](#misuse-of-channel-closing-semantics)
  - [Refs in Dependency Vector](#refs-in-dependency-vector)
  - [Improper Emptiness Check](#improper-emptiness-check)
  - [Unnecessary `into`](#unnecessary-into)
 
- [Module Boundaries & Data Contracts](#module-boundaries--data-contracts)
  - [Monolithic Namespace Split](#monolithic-namespace-split)
  - [Implicit Namespace Dependencies](#implicit-namespace-dependencies)
  - [Marker Protocol](#marker-protocol)
  - [Single-segment Namespace](#single-segment-namespace)
  - [Private Multimethods](#private-multimethods)
  - [Non-Idiomatic Record Construction](#non-idiomatic-record-construction)
  - [Excessive Refers](#excessive-refers)
  - [Map With Nil Values](#map-with-nil-values)
  - [Non-Idiomatic Parameter Binding](#non-idiomatic-parameter-binding)
    
- [Logic Flow & Readability](#logic-flow--readability)
  - [Conditional Build-Up](#conditional-build-up)
  - [Nested Forms](#nested-forms)
  - [Misused Threading](#misused-threading)
  - [Case with Non-Literal Test Values](#case-with-non-literal-test-values)
  - [Thread Ignorance](#thread-ignorance)
  - [Verbose Checks](#verbose-checks)
  - [Redundant `do` block](#redundant-do-block)

## State & Concurrency

This category focuses on how Clojure systems manage data identity over time, mutable state, and asynchronous execution.

### Immutability Violation

[Description: View description and example](README.md#immutability-violation) 

#### Source 1

**Type:** Google Forum

**URL:** [Forum - How to refactor a Java singleton to Clojure?](https://softwareengineering.stackexchange.com/questions/219780/how-to-refactor-a-java-singleton-to-clojure)<br>

**Excerpt:**

> “Mutable state totally destroys this concept, and with it, the advantages of pure code. Clojure doesn't force you to be pure, but it certainly makes it easy to do so”
     
#### Source 2

**Type:** GitHub Issue 

**URL:** [Issue #245 (taoensso/timbre)](https://github.com/taoensso/timbre/issues/245#issuecomment-381408303)<br>

**Excerpt:**

> "[...] this sounds like an anti-pattern to me. The namespace of a callsite is a real, immutable fact."

#### Source 3

**Type:** GitHub Issue 

**URL:** [Issue #1 (markmandel/brute)](https://github.com/markmandel/brute/issues/1#issuecomment-40436979)<br>

**Excerpt:**

> "You really just want to be passing in a immutable data structure into a function and then returning a new data structure which has the change in it."

[↑ Back to table of contents ↑](#table-of-contents) <br>

--- 

### Blocking Inside Go

[Description: View description and example](README.md#blocking-inside-go) 

#### Source 1

**Type:** GitHub Issue

**URL:** [Issue #303 (replikativ/datahike)](https://github.com/replikativ/datahike/issues/303#issuecomment-850150480)<br>

**Excerpt:**

> “This is a call to >!! or <!! inside a go block causing this, which effectively blocks an internal go dispatch thread, so clearly bad practice from whatever is doing that [...]”

[↑ Back to table of contents ↑](#table-of-contents) <br>

--- 

### Nested Atoms

[Description: View description and example](README.md#nested-atoms) 

#### Source 1

**Type:** GitHub Issue

**URL:** [Issue #6 (andrewleverette/clojulator)](https://github.com/andrewleverette/clojulator/issues/6#issue-2626611230)<br>

**Excerpt:**

> “[...] managing the UI state led to including that history atom in the global state atom. Nested atoms seem to be an anti-pattern.”

[↑ Back to table of contents ↑](#table-of-contents) <br>

--- 

### Misuse of Dynamic Scope

[Description: View description and example](README.md#misuse-of-dynamic-scope) 

#### Source 1

**Type:** GitHub Issue

**URL:** [Issue #202 (weavejester/codox)](https://github.com/weavejester/codox/issues/202#issuecomment-859137747)<br>

**Excerpt:**

> “In general, there's a very narrow set of circumstances where dynamic vars are a good idea.”

#### Source 2

**Type:** GitHub Source File

**URL:** [src/protektor/core.clj (jimrthy/protektor)](https://github.com/jimrthy/protektor/blob/7fec7073fe66207d3eb1a84d74851c5580f463cb/src/protektor/core.clj#L6)<br>

**Excerpt:**

> “All these dynamic "globals" are a definite code smell."

[↑ Back to table of contents ↑](#table-of-contents) <br>

--- 

### Overengineering with `core.async`

[Description: View description and example](README.md#overengineering-with-coreasync) 

#### Source 1

**Type:** GitHub Issue

**URL:** [Issue #11 (oliyh/re-graph)](https://github.com/oliyh/re-graph/issues/11#issuecomment-357652486)<br>

**Excerpt:**

> “Regarding core-async in general I've always found it to be an anti-pattern to use it for channels that only ever return one value, I think callback-fns or promises are better in these instances. This would mean you don't need core.async at all!"

[↑ Back to table of contents ↑](#table-of-contents) <br>

--- 

### Dynamically-Scoped Singleton Resource

[Description: View description and example](README.md#dynamically-scoped-singleton-resource) 

#### Source 1

**Type:** Google Blog

**URL:** [On the Perils of Dynamic Scope](https://stuartsierra.com/2013/03/29/perils-of-dynamic-scope/)<br>

**Excerpt:**

> “This brings me to one of my top anti-patterns in Clojure: the Dynamically-Scoped Singleton Resource (DSSR)."

#### Source 2

**Type:** GitHub Issue

**URL:** [Issue #2 (steffan-westcott/clj-otel)](https://github.com/steffan-westcott/clj-otel/issues/2#issuecomment-1221598024)<br>

**Excerpt:**

> “I should also point out that I am unsure of the merits of dynamic scoped objects. This article by Stuart Sierra makes some excellent points about why it may be considered a bad idea."

[↑ Back to table of contents ↑](#table-of-contents) <br>

--- 

### Unnecessary Laziness

[Description: View description and example](README.md#unnecessary-laziness) 

#### Source 1

**Type:** GitHub Issue

**URL:** [Issue #2219 (clj-kondo/clj-kondo)](https://github.com/clj-kondo/clj-kondo/issues/2219#issue-2005196658)<br>

**Excerpt:**

> “If intermediate lazy sequences are generally considered an anti-pattern then there could exist a more specific linter."

#### Source 2

**Type:** GitHub Issue

**URL:** [Issue #99 (taoensso/faraday)](https://github.com/taoensso/faraday/issues/99#issuecomment-268975919)<br>

**Excerpt:**

> “would suggest that using laziness when one doesn't specifically need/want laziness is an anti-pattern."

[↑ Back to table of contents ↑](#table-of-contents) <br>

---

## Environment & Idioms

This category focuses on how Clojure code interacts with the language’s core abstractions, runtime behavior, and idiomatic conventions.

### Unmanaged Resource I/O

[Description: View description and example](README.md#unmanaged-resource-io) 

#### Source 1

**Type:** GitHub Pull Request

**URL:** [PR #59728 (metabase/metabase)](https://github.com/metabase/metabase/pull/59728#pullrequestreview-2951949125)<br>

**Excerpt:**

> “Huge no-no to open up a java.io.Reader and not close it (use with-open here)"

[↑ Back to table of contents ↑](#table-of-contents)

--- 

### Relying on Load-Time Side Effects

[Description: View description and example](README.md#relying-on-load-time-side-effects) 

#### Source 1

**Type:** GitHub Issue

**URL:** [Issue #14 (clj-commons/kibit)](https://github.com/clj-commons/kibit/issues/14#issuecomment-284257729)<br>

**Excerpt:**

> “Relying on load time behavior for non-declarative operations is a huge antipattern."

[↑ Back to table of contents ↑](#table-of-contents)

--- 

### Namespace Load Side Effects

[Description: View description and example](README.md#namespace-load-side-effects) 

#### Source 1

**Type:** GitHub Issue

**URL:** [Issue #52004 (metabase/metabase)](https://github.com/metabase/metabase/issues/52004#issue-2781529758)<br>

**Excerpt:**

> “Do not use `require` in a top-level form outside of `ns`"

[↑ Back to table of contents ↑](#table-of-contents)

--- 

### Multiple Evaluation in Macros

[Description: View description and example](README.md#multiple-evaluation-in-macros) 

#### Source 1

**Type:** GitHub Pull Request

**URL:** [PR #225 (metabase/metabase)](https://github.com/clj-commons/manifold/pull/225#discussion_r1133205714)<br>

**Excerpt:**

> “Had you inserted value more than once, it could be evaluated multiple times, which you rarely want."

[↑ Back to table of contents ↑](#table-of-contents)

--- 

### Direct usage of `clojure.lang.RT`

[Description: View description and example](README.md#direct-usage-of-clojurelangrt) 

#### Source 1

**Type:** Google Website

**URL:** [Forum - Is interop with clojure.lang.RT an anti-pattern in clojure? / consider adding iter to clojure.core](https://ask.clojure.org/index.php/10303/interop-clojure-pattern-clojure-consider-adding-iter-clojure)<br>

**Excerpt:**

> “RT should be considered internal implementation and should not be called directly."

[↑ Back to table of contents ↑](#table-of-contents)

--- 

### Unnecessary Macros

[Description: View description and example](README.md#unnecessary-macros) 

#### Source 1

**Type:** Google Forum

**URL:** [Forum - Structuring Clojure applications](https://news.ycombinator.com/item?id=34052268)<br>

**Excerpt:**

> “Using macros when regular functions would do is a good example of that. It is absolutely possible to write impenetrable Clojure if you start doing weird things just because you can."

#### Source 2

**Type:** GitHub Issue

**URL:** [Issue #120 (nubank/state-flow)](https://github.com/nubank/state-flow/issues/120#issuecomment-630257053)<br>

**Excerpt:**

> “My suggestion is to avoid macros whenever possible because they are can be evil"

[↑ Back to table of contents ↑](#table-of-contents)

--- 

### Production `doall`

[Description: View description and example](README.md#production-doall) 

#### Source 1

**Type:** Google Blog

**URL:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/)<br>

**Excerpt:**

> “`doall` is a macro which forcefully realizes lazy sequences. It should not be used in production."

[↑ Back to table of contents ↑](#table-of-contents)

--- 

### Misuse of Channel Closing Semantics

[Description: View description and example](README.md#misuse-of-channel-closing-semantics) 

#### Source 1

**Type:** GitHub Pull Request

**URL:** [PR #63 (wkok/openai-clojure)](https://github.com/wkok/openai-clojure/pull/63#issuecomment-2134098405)<br>

**Excerpt:**

> “I do think that stopping on :done rather than a channel closing is non-idiomatic and bad practice."

[↑ Back to table of contents ↑](#table-of-contents)

--- 

### Refs in Dependency Vector

[Description: View description and example](README.md#refs-in-dependency-vector) 

#### Source 1

**Type:** GitHub Pull Request

**URL:** [PR #5658 (penpot/penpot)](https://github.com/penpot/penpot/pull/5658#discussion_r1930216262)<br>

**Excerpt:**

> “having the state reference object on deps and derefing on use-effect has no real meaning and is an anti pattern in any way"

[↑ Back to table of contents ↑](#table-of-contents)

--- 

### Improper Emptiness Check

[Description: View description and example](README.md#improper-emptiness-check) 

#### Source 1

**Type:** Google Blog

**URL:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/)<br>

**Excerpt:**

> “Don't use (not (empty? x))!"

[↑ Back to table of contents ↑](#table-of-contents)

--- 

### Unnecessary `into`

[Description: View description and example](README.md#unnecessary-into) 

#### Source 1

**Type:** Google Blog

**URL:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/)<br>

**Excerpt:**

> “`into` is a pretty useful function, but one often abused."
> The (mis)usage of into can usually be broken to three distinct cases:

[↑ Back to table of contents ↑](#table-of-contents)

--- 

## Module Boundaries & Data Contracts

This category focuses on the surface area of namespaces and modules, as well as on the clarity and predictability of the values exchanged across them.

### Monolithic Namespace Split

[Description: View description and example](README.md#monolithic-namespace-split) 

#### Source 1

**Type:** GitHub Issue

**URL:** [Issue #61 (technomancy/slamhound)](https://github.com/technomancy/slamhound/issues/61#issuecomment-30028399)<br>

**Excerpt:**

> “However, as each included file has an implicit dependency on the files that have been loaded before it, a simple find-word within a buffer is no longer likely to reveal the source of a symbol, nor every use of it within a namespace."

[↑ Back to table of contents ↑](#table-of-contents)

--- 

### Implicit Namespace Dependencies

[Description: View description and example](README.md#implicit-namespace-dependencies) 

#### Source 1

**Type:** GitHub Issue

**URL:** [Issue #14 (borkdude/grasp)](https://github.com/borkdude/grasp/issues/14#issue-780773295)<br>

**Excerpt:**

> “When analyzing a file such which has a refer all (which is known to be bad practice) the analyzer
does not add the extra information to possible unresolved symbols that can match the symbol"
 
#### Source 2

**Type:** GitHub Issue

**URL:** [Issue #2460 (clj-kondo/clj-kondo)](https://github.com/clj-kondo/clj-kondo/issues/2460#issuecomment-2566930153)<br>

**Excerpt:**

> “clj-kondo considers it bad practice to rely on letting other namespaces load library for you outside of the current one, hence it considers clojure.string not already loaded, even though clojure internally already has - which is just an implementation detail of clojure."

#### Source 3

**Type:** GitHub Pull Request

**URL:** [PR #1 (amexboy/multi-repo-project)](https://github.com/amexboy/multi-repo-project/pull/1#discussion_r285528892)<br>

**Excerpt:**

> “It's not really a good practice to refer whole namespaces, especially multiple in one namespace because it makes it really hard to find where functions come from. :refer [... :as ...] is a much better idea."
> Don't use :use. It makes things much hard to follow in order to save a couple of characters per call-site.


[↑ Back to table of contents ↑](#table-of-contents)

--- 

### Marker Protocol

[Description: View description and example](README.md#marker-protocol) 

#### Source 1

**Type:** GitHub Issue

**URL:** [Issue #262 (Tensegritics/ClojureDart)](https://github.com/Tensegritics/ClojureDart/pull/262#discussion_r1309809273)<br>

**Excerpt:**

> “Marker protocols are generally a code smell to me."

[↑ Back to table of contents ↑](#table-of-contents)

--- 

### Single-segment Namespace

[Description: View description and example](README.md#single-segment-namespace) 

#### Source 1

**Type:** GitHub Issue

**URL:** [Issue #35 (clj-easy/graal-build-time)](https://github.com/clj-easy/graal-build-time/issues/35#issuecomment-2192231474)<br>

**Excerpt:**

> “As single-segment namespaces are an anti-pattern in Clojure, I'm happy not to invest any time in finding a way to make them work"

#### Source 2

**Type:** GitHub Issue

**URL:** [Issue #42 (boot-clj/boot-cljs)](https://github.com/boot-clj/boot-cljs/issues/42#issuecomment-73830121)<br>

**Excerpt:**

> “Closing as single segment namespace are now officially a bad practice."

#### Source 3

**Type:** GitHub Pull Request

**URL:** [PR #22 (deercreeklabs/lancaster)](https://github.com/deercreeklabs/lancaster/pull/22#issue-1378124990)<br>

**Excerpt:**

> “As far as I know, single-segment namespaces are bad practice anyways"

#### Source 4

**Type:** GitHub Commit

**URL:** [Commit (clojure-expectations/expectations)](https://github.com/clojure-expectations/expectations/commit/a0370bf6ae74fa7735b0ba6917692b8c07eba3e0)<br>

**Excerpt:**

> “one-segment namespaces are a bad practice anyway."

[↑ Back to table of contents ↑](#table-of-contents)

--- 

### Private Multimethods

[Description: View description and example](README.md#private-multimethods) 

#### Source 1

**Type:** GitHub Issue

**URL:** [Commit (greglook/cljstyle)](https://github.com/greglook/cljstyle/commit/a52ff57f52670ef2866d3a574557ef9c5198805e)<br>

**Excerpt:**

> “Private multimethods are a code smell."

[↑ Back to table of contents ↑](#table-of-contents)

--- 

### Non-Idiomatic Record Construction

[Description: View description and example](README.md#non-idiomatic-record-construction) 

#### Source 1

**Type:** GitHub Source File

**URL:** [src/koans/19_datatypes.clj (corpix/clojure-koans)](https://github.com/corpix/clojure-koans/blob/372ba7d2deb6b78d3c6a2fa0a7ed0d0c279b19f7/src/koans/19_datatypes.clj#L27C7-L27C113)<br>

**Excerpt:**

> “fuckin beautiful, antipattern in the koans https://stuartsierra.com/2015/05/17/clojure-record-constructors"

#### Source 2

**Type:** Google Blog

**URL:** [Record Constructors](https://stuartsierra.com/2015/05/17/clojure-record-constructors/)<br>

**Excerpt:**

> “Interop syntax is for interop with Java libraries."
> "Since Clojure version 1.3, defrecord and deftype automatically create constructor functions. Use those instead of interop syntax."

[↑ Back to table of contents ↑](#table-of-contents)

--- 

### Excessive Refers

[Description: View description and example](README.md#excessive-refers) 

#### Source 1

**Type:** GitHub Issue

**URL:** [Issue #305_datatypes.clj (clojure-emacs/refactor-nrepl)](https://github.com/clojure-emacs/refactor-nrepl/issues/305#issue-930892797)<br>

**Excerpt:**

> “Having tons of referred symbols is an anti-pattern anyway, so we should nudge people toward not doing that."

#### Source 2

**Type:** GitHub Issue

**URL:** [Issue #342_datatypes.clj (clj-kondo/clj-kondo)](https://github.com/clj-kondo/clj-kondo/issues/342#issuecomment-511108107)<br>

**Excerpt:**

> “Clojure style guide recommends `:as` or `:refer [...]` over `:refer :all`"

[↑ Back to table of contents ↑](#table-of-contents)

--- 

### Map With Nil Values

[Description: View description and example](README.md#map-with-nil-values) 

#### Source 1

**Type:** Google Blog

**URL:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/)<br>

**Excerpt:**

> “Clojure maps are collections, not slots. Combined with nil's meaning being "nothing", nil values inside maps are confusing"
> "Try to avoid inserting nil values into a map."

[↑ Back to table of contents ↑](#table-of-contents)

--- 

### Non-Idiomatic Parameter Binding

[Description: View description and example](README.md#non-idiomatic-parameter-binding) 

#### Source 1

**Type:** GitHub Pull Request

**URL:** [PR #205 (weavejester/codox)](https://github.com/weavejester/codox/pull/205#discussion_r711587308)<br>

**Excerpt:**

> “`& [ns]` is something we should replace with proper argument arities (or omitted entirely, if they are never used), as it's actually an anti-pattern"

[↑ Back to table of contents ↑](#table-of-contents)

--- 

## Logic Flow & Readability

This category focuses on the visual and cognitive path through which data transformations are expressed in code.    

### Conditional Build-Up

[Description: View description and example](README.md#conditional-build-up) 

#### Source 1

**Type:** Google Blog

**URL:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/)<br>

**Excerpt:**

> “Conditional Build-Up"

[↑ Back to table of contents ↑](#table-of-contents)

--- 

### Nested Forms

[Description: View description and example](README.md#nested-forms)

#### Source 1

**Type:** Google Blog

**URL:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/)<br>

**Excerpt:**

> “Plenty of macros with binding forms don't need to be nested"

#### Source 2

**Type:** GitHub Pull Request

**URL:** [PR #1107 (nasa/Common-Metadata-Repository)](https://github.com/nasa/Common-Metadata-Repository/pull/1107#discussion_r524547709)<br>

**Excerpt:**

> “Nested lets are a code smell and this could easily be broken off into another function."

[↑ Back to table of contents ↑](#table-of-contents)

--- 

### Misused Threading

[Description: View description and example](README.md#misused-threading)

#### Source 1

**Type:** GitHub Pull Request

**URL:** [PR #97 (amperity/lein-monolith)](https://github.com/amperity/lein-monolith/pull/97#discussion_r1612337849)<br>

**Excerpt:**

> “Also, stylistically, I think this is an example of a threading antipattern - thread-first and thread-last are best used to chain together successive transformations on a similar argument type."

[↑ Back to table of contents ↑](#table-of-contents)

--- 

### Case with Non-Literal Test Values

[Description: View description and example](README.md#case-with-non-literal-test-values)

#### Source 1

**Type:** GitHub Pull Request

**URL:** [PR #16781 (status-im/status-legacy)](https://github.com/status-im/status-legacy/pull/16781#discussion_r1275151733)<br>

**Excerpt:**

> “`case` should be used only with literals"

[↑ Back to table of contents ↑](#table-of-contents)

--- 

### Thread Ignorance

[Description: View description and example](README.md#thread-ignorance)

#### Source 1

**Type:** Google Blog

**URL:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/)<br>

**Excerpt:**

> “Avoid trivial threading"
> "And remember to thread with style"

[↑ Back to table of contents ↑](#table-of-contents)

--- 

### Verbose Checks

[Description: View description and example](README.md#verbose-checks)

#### Source 1

**Type:** Google Blog

**URL:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/)<br>

**Excerpt:**

> “Clojure has functions covering some common use cases when working with numbers which both perform and convey intent better."
> "Same case with numbers, no need to compare to booleans and nil."

[↑ Back to table of contents ↑](#table-of-contents)

--- 

### Redundant `do` block

[Description: View description and example](README.md#redundant-do-block)

#### Source 1

**Type:** Google Blog

**URL:** [Idiomatic Clojure: Code Smells](https://bsless.github.io/code-smells/)<br>

**Excerpt:**

> “Some expressions have implicit `do` blocks in them, making it unnecessary to use a `do` block."

[↑ Back to table of contents ↑](#table-of-contents)

--- 

Contributions are welcome via Issues and Pull Requests.
