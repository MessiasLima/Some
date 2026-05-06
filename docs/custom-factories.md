# Custom Factories

Register custom factory functions to override how specific types are generated. Factories let you pin certain types to known values, random ranges, or format rules — useful for test scenarios where you need deterministic or domain-specific data.

## Basic registration

Use `register` inside a `some {}` block to override how a type is generated for a single call.

```kotlin
data class Product(val name: String, val price: Int)

val someProduct = some<Product> {
    register(String::class) { "custom-value" }
    register(Int::class) { 42 }
}

// Product(name=custom-value, price=42)
```

Custom factories take priority over built-in resolvers. When a type has a registered factory, the resolver chain is bypassed entirely for that type.

## Using FixtureContext

The factory lambda receives a `FixtureContext` receiver that exposes the current configuration. This lets your factory adapt to settings like string strategy, nullable behavior, and collection sizes without hardcoding them.

| Property | Type | Description |
|----------|------|-------------|
| `random` | `Random` | Random instance (respects `seed`) |
| `nullableStrategy` | `NullableStrategy` | Current nullable strategy |
| `stringStrategy` | `StringStrategy` | Current string strategy |
| `collectionStrategy` | `CollectionStrategy` | Current collection strategy |

```kotlin
val someWithFixture = someSetup {
    
    register(String::class) {
        when (stringStrategy) {
            is StringStrategy.Random -> "val-${random.nextInt(1000)}"
            is StringStrategy.Uuid -> "custom-${random.nextLong()}"
            is StringStrategy.Readable -> "cfg-${random.nextInt(9000) + 1000}"
        }
    }
    
    register(Int::class) { random.nextInt(1, 100) }
}
```

## Custom factories for your types

Beyond primitives, you can register factories for your own domain types. This is useful for types that need specific construction rules, like value objects with validation constraints.

```kotlin
data class Email(val value: String)

val someWithEmail = someSetup {
    register(Email::class) {
        Email("user${random.nextInt(1000)}@example.com")
    }
}

data class Account(val email: Email, val name: String)
val account = someWithEmail<Account>()
// Account(email=Email(user42@example.com), name=asdkjfh)
```

When `Account` is generated, `Email` is resolved by the custom factory while `name` (a plain `String`) falls through to the built-in string resolver.

## Aggregated config preserves base factories

When you call an existing `someSetup` with additional overrides, the original configuration is **snapshot** — subsequent calls to the base instance are unaffected. This lets you create a shared base config and layer overrides per test without side effects.

```kotlin
val baseSome = someSetup {
    register(String::class) { "base-string" }
}

// Override String factory just for this call
val overridden: Product = baseSome {
    register(String::class) { "overridden" }
}
// overridden.name == "overridden"
// overridden.price is generated normally (Int resolves via built-in resolver)

// Base config is unchanged — subsequent calls still use "base-string"
val stillBase: Product = baseSome()
// stillBase.name == "base-string"

// Even the overridden call's scope is isolated from others
val anotherOverride: Product = baseSome {
    register(Int::class) { 99 }
}
// anotherOverride.price == 99
// anotherOverride.name == "base-string"
```
