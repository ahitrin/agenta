# Migrate to Java-based counters

*Motivation*; simpler and faster code.
We want to replace immutable 'counter' values originally implemented in pure Clojure with mutable Java classes.
This must reduce amount of new objects creations and make some pieces of clojure logic simpler.

## Implementation plan

1. Create a new Java class `agenta.domain.Counter`.
It must have similar methods as clojure functions we have in `src/agenta/counter.clj`:

* Constructor with 2 arguments; `Counter(int initValue, int maxValue)`. Save 'initValue` into 'currentValue' field and save `maxValue` as field.

* Constructor with 1 argument: `Counter(int maxValue)`. Just an equivalent of `Counter(maxValue, maxValue)`.

* Method `boolean isReady()`. Returns `true` when `currentValue` equals zero.

* Method `void tick()`. Decrease `currentValue` when it's positive.

* Method `void reset()``. Sets `currentValue` to be equal `maxValue` again.

Acceptance criteria: `lein compile` bash command must pass.

2. Implement *clojure* tests on the behavor of new `Counter` class.
Do not write tests in Java!
Instead, write a pure clojure code as `tests/agenta/domain_test.clj.
Use namespace name `agenta.doma9n-test`.
Implement typical use cases for the `Counter` class.

Acceptance criteria: `lein test` bash command must pass.

3. Rework `src/agenta/counter.clj` to use the new `Counter` class:

* Function `make` must create an instance of `Counter` instead of cloiure vector that's cureently used.

* Function `ready?` must call `isReady()` method of the provided object (it must look as `(.isReady counter)` in Clojure notation).

* Function `tick` must call `tick()``method of the provided object and then return it.

* Function `reset` must call `reset()` method of the provided object and then return it.

Acceptance criteria: first, `lein test` bash command must pass.
Then, `make smoke` commamd must finish succesfilly.

4. Next step will be provided a bit later.
