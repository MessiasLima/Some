---
icon: lucide/settings
---
# Configuration

Some works with no configuration, but every generation call can be customized when a test needs deterministic output, different null behavior, constrained collections, or custom factories.

Configuration is written inside `some {}` or `someSetup {}` blocks. Register strategies or factories in the block:

```kotlin
val user = some<User> {
    seed = 12345L
    strategy(StringStrategy.Readable)
    strategy(NullableStrategy.NeverNull)
    factory(Email::class) { Email("test@example.com") }
}
```

## One-Off Configuration

Use `some<T> { ... }` when a configuration only applies to one generated value.

```kotlin
val user = some<User> {
    strategy(NullableStrategy.AlwaysNull)
    strategy(StringStrategy.Readable)
}
```

Inline configuration does not change the defaults used by later `some<T>()` calls.

## Reusable Configuration

Use `someSetup { ... }` when many generated values should share the same configuration.

```kotlin
val some = someSetup {
    seed = 12345L
    strategy(StringStrategy.Uuid)
    strategy(CollectionStrategy(3..7))
}

val user = some<User>()
val order = some<Order>()
```

This is useful in test suites where several fixtures should follow the same rules.

## Building blocks

The configuration DSL is intentionally small:

| Setting | Purpose |
|---------|---------|
| `seed = ...` | Reproduce the same generated values across runs |
| `strategy(...)` | Override how a built-in type is generated |
| `factory(...)` | Override generation for an entire type |
| `property(...)` | Override generation for one constructor property |

## Per-Call Overrides

Reusable configurations can be overridden for a single call. The base instance is copied before the override is applied, so later calls still use the original setup.

```kotlin
val baseSome = someSetup {
    strategy(NullableStrategy.NeverNull)
    strategy(StringStrategy.Readable)
}

val result: Person = baseSome {
    strategy(NullableStrategy.AlwaysNull)
}

// baseSome still uses NeverNull and Readable strings
val stillNeverNull: Person = baseSome()
```

## Reproducible Data

Set `seed` when a test should produce the same values every run.

```kotlin
val some1 = someSetup { seed = 12345L }
val some2 = someSetup { seed = 12345L }

some1<User>() == some2<User>()  // true
```

Without a seed, values can vary between runs.

## Related Docs

- Use [Strategies](../strategies/index.md) for the built-in defaults and strategy behavior.
- Use [Supported Types](../supported-types.md) for the shared type coverage.
- Use [Type and Property Factories](../custom-factories.md) for custom generation rules.
- Use [Android apps](../artifacts/some-android.md) or [Kotest integration](../artifacts/some-kotest.md) when you need platform-specific dependency guidance.
