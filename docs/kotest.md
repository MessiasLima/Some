---
icon: lucide/flask-conical
---
# Kotest integration

Use `dev.appoutlet:some-kotest` when you want Kotest `Arb` integration for Some.

`some-kotest` is an add-on integration. It should be used alongside either `some-core` or `some-android`.

## Installation

### JVM / Kotlin

```kotlin
testImplementation("dev.appoutlet:some-core:{version}")
testImplementation("dev.appoutlet:some-kotest:{version}")
```

### Android

```kotlin
testImplementation("dev.appoutlet:some-android:{version}")
testImplementation("dev.appoutlet:some-kotest:{version}")
```

## When to use it

- Kotest property tests that should generate fixtures through Some
- Test suites that want `Arb.some<T>()` instead of manually building Arbs

## Artifact-specific behavior

- Adds `Arb.some<T>()`
- Accepts the same configuration DSL used by `some<T>()` and `someSetup {}`
- Creates one reusable `Some` generator per `Arb.some<T>()` call
- Uses `Some`'s configured seed for reproducibility when `seed = ...` is set in the DSL

## Usage

```kotlin
import dev.appoutlet.some.kotest.some
import io.kotest.property.Arb
import io.kotest.property.checkAll

checkAll(Arb.some<User>(), Arb.some<Order>()) { user, order ->
    // assertions
}
```

`Arb.some<T>()` reuses a single configured `Some` generator for the lifetime of the arb.
If you need reproducible values across runs, configure `Some` with an explicit seed.

```kotlin
checkAll(
    Arb.some<User> {
        seed = 12345L
        strategy(StringStrategy.Readable)
        strategy(NullableStrategy.NeverNull)
    }
) { user ->
    // assertions
}
```

## Coordinating with Kotest randomness

`Arb.some<T>()` creates one reusable `Some` generator for the lifetime of the arb.
That generator uses `Some`'s own random source and does not consume Kotest's active
per-sample `RandomSource`.

If you want to coordinate `Some` with a Kotest `RandomSource`, create or reuse a
seed and pass it into the `Some` DSL:

```kotlin
import io.kotest.property.RandomSource

val randomSource = RandomSource.default()

val arb = Arb.some<User> {
    seed = randomSource.seed
}
```

This makes `Some` use a random stream derived from the same seed value.

For the shared configuration model, see [Configuration](configuration.md) and [Strategies](strategies/index.md).
