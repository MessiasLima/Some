# Configuration

All configuration is done through `SomeConfig`. Every option has sensible defaults so you can start using `some()` immediately without any setup.

## The SomeConfig class

```kotlin
data class SomeConfig(
    var nullableStrategy: NullableStrategy = NullableStrategy.Random(),
    var stringStrategy: StringStrategy = StringStrategy.Random(),
    var collectionStrategy: CollectionStrategy = CollectionStrategy(),
    var seed: Long? = null,
    val factories: MutableMap<KClass<*>, FixtureContext.() -> Any?> = mutableMapOf(),
)
```

## Creating configuration objects

### Global configuration with `someSetup`

Use `someSetup {}` to create a reusable `Some` instance with a shared configuration:

```kotlin
val some = someSetup {
    seed = 12345L
    stringStrategy = StringStrategy.Uuid
    collectionStrategy = CollectionStrategy(3..7)
}

val user = some<User>()
val order = some<Order>()
```

### Inline configuration with `some {}`

Pass a configuration lambda directly to `some()` for one-off overrides:

```kotlin
val user = some<User> {
    nullableStrategy = NullableStrategy.AlwaysNull
    stringStrategy = StringStrategy.Readable
}
```

The inline form creates a copy of the default configuration, applies your overrides, and generates a single instance — without affecting the global defaults.

### Copying configurations

`SomeConfig.copy()` creates a deep copy, including a fresh copy of the `factories` map. This prevents shared mutable state between config instances:

```kotlin
val base = SomeConfig().apply { stringStrategy = StringStrategy.Uuid }
val custom = base.copy(seed = 999L)
```

## Default configuration

| Property | Default | Description | Docs                                         |
|----------|---------|-------------|----------------------------------------------|
| `nullableStrategy` | `NullableStrategy.Random()` | 50% chance of `null` for nullable types | [NullableStrategy](nullable-strategy.md)     |
| `stringStrategy` | `StringStrategy.Random()` | Random lowercase alphabetic, 8 characters | [StringStrategy](string-strategy.md)         |
| `collectionStrategy` | `CollectionStrategy()` | Collections of 1 to 5 elements | [CollectionStrategy](collection-strategy.md) |
| `seed` | `null` | Uses non-deterministic `Random.Default` | —                                            |

## Seed

Setting `seed` produces deterministic output — the same seed always generates the same values:

```kotlin
val some1 = someSetup { seed = 12345L }
val some2 = someSetup { seed = 12345L }

some1<User>() == some2<User>()  // true
```

Without a seed, `Random.Default` is used, meaning each generation produces different random values.

Internally, `SomeConfig.buildRandom()` creates a seeded `kotlin.random.Random` if `seed` is set, or falls back to `Random.Default`.

## Custom factories

`SomeConfig` holds a `factories` map where you can register custom factory functions for specific types:

```kotlin
val some = someSetup {
    register(Email::class) { Email("${some<String>()}@example.com") }
    register(CustomerId::class) { CustomerId(some<UUID>().toString()) }
}
```

See [Custom Factories](../custom-factories.md) for detailed documentation.
