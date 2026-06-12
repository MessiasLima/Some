---
icon: lucide/settings
---
# Configuration

Some works with no configuration, but each generation call can be customized when a test needs deterministic output, different null behavior, readable strings, fixed collection sizes, or custom factories.

Configuration is written inside `some {}` or `someSetup {}` blocks. Register strategies or factories in the block:

```kotlin
val user = some<User> {
    seed = 12345L
    strategy(StringStrategy.Readable)
    strategy(NullableStrategy.NeverNull)
    strategy(CollectionStrategy(3..7))
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

## Defaults

| Strategy | Default | Description | More |
|----------|---------|-------------|------|
| `NullableStrategy` | `NullableStrategy.NullOnCircularReference` | Emits `null` for nullable circular references | [NullableStrategy](nullable-strategy.md) |
| `StringStrategy` | `StringStrategy.Random()` | Random lowercase alphabetic strings | [StringStrategy](string-strategy.md) |
| `CollectionStrategy` | `CollectionStrategy()` | Collections with 1 to 5 elements | [CollectionStrategy](collection-strategy.md) |
| `DefaultValueStrategy` | `DefaultValueStrategy.UseDefault` | Uses Kotlin defaults for optional parameters | [DefaultValueStrategy](default-value-strategy.md) |
| `seed` | `null` | Uses non-deterministic `Random.Default` | — |

## Reproducible Data

Set `seed` when a test should produce the same values every run.

```kotlin
val some1 = someSetup { seed = 12345L }
val some2 = someSetup { seed = 12345L }

some1<User>() == some2<User>()  // true
```

Without a seed, values can vary between runs.

## Common Recipes

### No Null Values

```kotlin
val user = some<User> {
    strategy(NullableStrategy.NeverNull)
}
```

### Readable Strings

```kotlin
val user = some<User> {
    strategy(StringStrategy.Readable)
}
```

### Small Collection Sizes

```kotlin
val order = some<Order> {
    strategy(CollectionStrategy(2..3))
}
```

### Generate All Optional Values

```kotlin
val user = some<User> {
    strategy(DefaultValueStrategy.Generate)
}
```

### Custom Factories

Use `factory` to customize a whole type, and `property` to customize one constructor property.

```kotlin
val some = someSetup {
    factory(Email::class) { Email("user${random.nextInt(1000)}@example.com") }
    property(User::role) { "Admin" }
}
```

See [Type and Property Factories](../custom-factories.md) for detailed documentation.
