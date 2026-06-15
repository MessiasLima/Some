---
icon: lucide/file-type
---
# Supported Types

Some resolves common Kotlin and Java types with zero configuration. Nullable variants (`T?`) are supported everywhere — [NullableStrategy](configuration/nullable-strategy.md) controls whether `null` is emitted.

For types not listed here, register a [custom factory](custom-factories.md).

## Reference

| Type | Usage | Notes |
|------|-------|-------|
| **Primitives** | | |
| `String` | `some<String>()` | See [StringStrategy](configuration/string-strategy.md) |
| `Int` | `some<Int>()` | |
| `Long` | `some<Long>()` | |
| `Double` | `some<Double>()` | |
| `Float` | `some<Float>()` | |
| `Boolean` | `some<Boolean>()` | |
| `Char` | `some<Char>()` | |
| `Byte` | `some<Byte>()` | |
| `Short` | `some<Short>()` | |
| `Number` | `some<Number>()` | Resolves to a random concrete numeric type (`Int`, `Long`, `Double`, `Float`, or `Short`) |
| **Standard Library** | | |
| `kotlin.uuid.Uuid` | `some<Uuid>()` | Requires `@OptIn(ExperimentalUuidApi::class)` |
| `java.util.UUID` | `some<UUID>()` | |
| `java.util.Optional<T>` | `some<Optional<String>>()` | See [NullableStrategy](configuration/nullable-strategy.md) |
| `kotlin.time.Duration` | `some<Duration>()` | |
| `java.time.Duration` | `some<java.time.Duration>()` | |
| `kotlin.time.Instant` | `some<Instant>()` | |
| `java.time.Instant` | `some<java.time.Instant>()` | |
| `java.math.BigDecimal` | `some<BigDecimal>()` | |
| `java.math.BigInteger` | `some<BigInteger>()` | |
| `java.time.LocalDate` | `some<LocalDate>()` | |
| `java.time.LocalDateTime` | `some<LocalDateTime>()` | |
| `java.time.ZonedDateTime` | `some<ZonedDateTime>()` | |
| **Collections** | | |
| `List<T>` | `some<List<String>>()` | See [CollectionStrategy](configuration/collection-strategy.md) |
| `MutableList<T>` | `some<MutableList<Int>>()` | |
| `Set<T>` | `some<Set<String>>()` | |
| `MutableSet<T>` | `some<MutableSet<String>>()` | |
| `Map<K, V>` | `some<Map<String, Int>>()` | |
| `MutableMap<K, V>` | `some<MutableMap<String, Int>>()` | |
| `Array<T>` | `some<Array<String>>()` | |
| **Kotlin constructs** | | |
| Data classes | `some<MyDataClass>()` | |
| Sealed classes/interfaces | `some<MySealed>()` | Random subclass, including `object` variants |
| Enums | `some<MyEnum>()` | Random constant |
| Value classes | `some<MyValue>()` | Unwraps to the underlying type |
| Object singletons | `some<MyObject>()` | Returns the singleton instance |
| Generics | `some<Box<User>>()` | Recursively resolves type arguments |

## Custom types

Classes that don't have a primary constructor, require special construction logic, or are third-party types need a custom type factory:

```kotlin
data class Email(val value: String)

someSetup {
    factory(Email::class) { Email("user${random.nextInt(1000)}@example.com") }
}
```

See [Type and Property Factories](custom-factories.md) for more.
