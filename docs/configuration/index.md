---
icon: lucide/settings
---
# Configuration

All configuration is done through `SomeConfigBuilder`. Every option has sensible defaults so you can start using `some()` immediately without any setup.

## The SomeConfigBuilder class

Configuration lambdas (`someSetup {}`, `some {}`) receive a `SomeConfigBuilder` receiver with mutable properties. Calling `build()` produces an immutable `SomeConfig`.

```kotlin
class SomeConfigBuilder {
    var nullableStrategy: NullableStrategy = NullableStrategy.NullOnCircularReference
    var stringStrategy: StringStrategy = StringStrategy.Random()
    var collectionStrategy: CollectionStrategy = CollectionStrategy()
    var seed: Long? = null

    fun <T : Any> register(kClass: KClass<T>, factory: FixtureContext.() -> T)
    fun build(): SomeConfig
}
```

The resulting `SomeConfig` is immutable:

```kotlin
data class SomeConfig(
    val nullableStrategy: NullableStrategy = NullableStrategy.NullOnCircularReference,
    val stringStrategy: StringStrategy = StringStrategy.Random(),
    val collectionStrategy: CollectionStrategy = CollectionStrategy(),
    val seed: Long? = null,
    val factories: Map<KClass<*>, FixtureContext.() -> Any?> = emptyMap(),
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

The inline form creates a new builder from the default configuration, applies your overrides, and generates a single instance — without affecting the global defaults.

### Aggregated configuration

You can override configuration on a per-call basis without mutating the base instance:

```kotlin
val baseSome = someSetup {
    seed = 42L
    nullableStrategy = NullableStrategy.NeverNull
}

// Override for a single call — base config is NOT mutated
val result: Person = baseSome {
    nullableStrategy = NullableStrategy.AlwaysNull
}

// baseSome still uses NeverNull
val stillNeverNull: Person = baseSome()
```

## Default configuration

| Property | Default | Description | Docs                                         |
|----------|---------|-------------|----------------------------------------------|
| `nullableStrategy` | `NullableStrategy.NullOnCircularReference` | `null` for nullable circular references | [NullableStrategy](nullable-strategy.md)     |
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

`SomeConfigBuilder` provides a `register` function to add custom factory functions for specific types:

```kotlin
val some = someSetup {
    register(Email::class) { Email("${some<String>()}@example.com") }
    register(CustomerId::class) { CustomerId(some<UUID>().toString()) }
}
```

See [Custom Factories](../custom-factories.md) for detailed documentation.
