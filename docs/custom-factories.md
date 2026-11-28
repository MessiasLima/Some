---
icon: lucide/factory
---
# Type and Property Factories

Factories let you override generated values when the built-in resolvers are too broad for a test or too generic for your domain. Some supports two levels of overrides: **type factories** and **property factories**.

Use a **type factory** when every occurrence of a type should be generated in a custom way. Use a **property factory** when only one constructor property should be overridden and every other property should keep using normal generation.

| Factory | API | Scope | Typical use |
|---------|-----|-------|-------------|
| Type factory | `factory(MyType::class) { ... }` | Every generated value of `MyType`, including all generic instantiations | Value objects, primitives in a scenario, third-party types |
| Type-level factory | `factory<MyType<Arg>>() { ... }` | Only the exact requested type | One generic instantiation such as `List<Number>` |
| Property factory | `property(User::email) { ... }` | One constructor property on one class | Stable IDs, readable names, one field in a large object |

## Type factories

Use `factory` inside a `some {}` or `someSetup {}` block to override how a type is generated.

```kotlin
data class Product(val name: String, val price: Int)

val product = some<Product> {
    factory(String::class) { "custom-value" }
    factory(Int::class) { 42 }
}

// Product(name=custom-value, price=42)
```

Type factories take priority over built-in resolvers. When a type has a registered type factory, Some invokes that factory directly instead of continuing through the resolver chain.

This means a type factory is intentionally broad. In the example above, every `String` needed while generating `Product` becomes `"custom-value"`, not only `Product.name`.

## Domain type factories

Type factories are useful for domain types that need validation, formatting, or construction rules that Some cannot infer from the constructor alone.

```kotlin
data class Email(val value: String)
data class Account(val email: Email, val name: String)

val someWithEmail = someSetup {
    factory(Email::class) {
        Email("user${random.nextInt(1000)}@example.com")
    }
}

val account = someWithEmail<Account>()
// Account(email=Email(user42@example.com), name=asdkjfh)
```

When `Account` is generated, `Email` is resolved by the type factory while `name` falls through to the built-in `String` resolver.

## Generic-aware type factories

A class-level factory is intentionally broad: `factory(List::class)` replaces every list regardless of its element
type. When you need to override one generic instantiation, register the factory against the complete type instead:

```kotlin
val someWithNumbers = someSetup {
    factory<List<Number>> { listOf(1, 2.5, 3L) }
}

val numbers: List<Number> = someWithNumbers.some()
val strings: List<String> = someWithNumbers.some() // normal generation
```

The reified `factory<T> { ... }` overload registers against `typeOf<T>()`, so generic arguments and nullability are
part of the match. The lambda result must be assignable to `T` at compile time. You can also pass a `KType`
explicitly when the type is only known at runtime:

```kotlin
val elementType = typeOf<String>()
someSetup {
    factory(List::class.createType(listOf(KTypeProjection.invariant(elementType)))) {
        listOf("runtime-type")
    }
}
```

### Type-level matching precedence

For a requested type, Some checks factories in this order:

1. An exact `KType` match from `factory<T> { ... }` or `factory(kType) { ... }`.
2. If the request is nullable, the same type with only its outer nullability removed. This is why a factory for
   `List<Number>` also applies to `List<Number>?`.
3. The existing `KClass` match from `factory(MyType::class) { ... }`.

A type-level factory therefore wins over a class-level factory for the same classifier, while the class-level factory
continues to apply to every other instantiation. Nullable type-level requests also follow the same outer-nullability
fallback before the nullable strategy runs, matching the existing class-level factory behavior.

## Property factories

Use `property` when you only want to override one property. The rest of the object is still generated normally.

```kotlin
data class User(
    val id: Int,
    val name: String,
    val role: String,
    val age: Int = 25
)

val user = some<User> {
    property(User::name) { "Alice" }
    property(User::role) { "Admin" }
}

// User(id=834920, name=Alice, role=Admin, age=25)
```

Property factories are matched by the owning class and property name. They are applied while Some calls the primary constructor, so optional constructor parameters that are not overridden can still use their Kotlin default values.

## Type factory vs property factory

If both a type factory and property factory could affect the same generated class, the type factory wins. This happens because type factories are resolved before Some reaches data-class constructor generation.

```kotlin
data class User(val name: String, val role: String)

val user = some<User> {
    factory(User::class) {
        User(name = "TypeFactory", role = "TypeFactory role")
    }

    property(User::name) { "PropertyFactory" }
}

// user.name == "TypeFactory"
```

The `User` type factory builds the whole `User`, so the `User::name` property factory is never used for that generation.

## Using FixtureContext

Both type factories and property factories receive a `FixtureContext` receiver. Use it to respect the current configuration instead of hardcoding every detail.

| Property | Type | Description |
|----------|------|-------------|
| `random` | `Random` | Random instance, including the configured `seed` |
| `resolutionStack` | `List<KType>` | Types currently being resolved |
| `strategyProvider` | `StrategyProvider` | Access to all registered strategies |

```kotlin
data class Customer(val id: String, val age: Int)

val customer = some<Customer> {
    property(Customer::id) {
        when (val s = strategyProvider.get<StringStrategy>()) {
            is StringStrategy.Random -> "customer-${random.nextInt(1000)}"
            is StringStrategy.Uuid -> "customer-${random.nextLong()}"
            is StringStrategy.Readable -> "customer-${random.nextInt(9000) + 1000}"
        }
    }

    factory(Int::class) { random.nextInt(18, 100) }
}
```

## Reusable setup

Use `someSetup` when the same factories should be shared across multiple generated values.

```kotlin
val someWithDefaults = someSetup {
    factory(String::class) { "base-string" }
    property(User::role) { "Member" }
}

val user = someWithDefaults<User>()
val product = someWithDefaults<Product>()
```

You can still override a reusable setup for one call. The base configuration is copied before the inline overrides are applied, so later calls to the base instance are unchanged.

```kotlin
val overriddenUser = someWithDefaults<User> {
    property(User::name) { "Bob" }
}

val stillBase = someWithDefaults<User>()
```

## Choosing the right override

Prefer property factories for test readability when only one field matters. Prefer type factories when a type has domain rules, validation constraints, or a value format that should be consistent wherever that type appears.

If you are building a library or need to provide resolver logic for internal application types, consider shipping a [custom resolver](custom-resolvers.md) instead. Custom resolvers are discovered automatically via `ServiceLoader` and don't require consumers to write any configuration.
