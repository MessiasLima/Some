---
icon: lucide/flask-conical
---
# some-kotest

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
- Uses Kotest's `RandomSource` to keep generated values reproducible

## Usage

```kotlin
import dev.appoutlet.some.kotest.some
import io.kotest.property.Arb
import io.kotest.property.checkAll

checkAll(Arb.some<User>(), Arb.some<Order>()) { user, order ->
    // assertions
}
```

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

For the shared configuration model, see [Configuration](../configuration/index.md) and [Strategies](../strategies/index.md).
