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

Contributions are welcome via Issues and Pull Requests.
