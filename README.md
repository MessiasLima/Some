# Some

> [!WARNING]
> **Early Development Stage:** This project is in its early stages and is not yet ready for production use. APIs are subject to change, and it has not been officially released.

A Kotlin JVM library that generates populated instances of any Kotlin class for testing purposes, with zero configuration required.

## Usage

### Zero Configuration

The simplest way to use Some is with the top-level `some<T>()` function:

```kotlin
import dev.appoutlet.some.some

data class User(val name: String, val age: Int)
data class Address(val street: String, val city: String)
data class Person(val name: String, val address: Address)

// Generate a simple data class
val user: User = some<User>()

// Generate a nested data class  
val person: Person = some<Person>()

// Generate a list of data classes
val users: List<User> = some<List<User>>()

// Generate a sealed class (picks random subclass)
sealed class PaymentMethod {
    data class Card(val last4: String) : PaymentMethod()
    data class BankTransfer(val iban: String) : PaymentMethod()
    object Cash : PaymentMethod()
}
val payment: PaymentMethod = some<PaymentMethod>()
```

### Configuration

For more control, use the `some {}` builder function:

```kotlin
import dev.appoutlet.some.some
import dev.appoutlet.some.config.*

// Configure nullable strategy
val someWithNulls = some {
    nullableStrategy = NullableStrategy.AlwaysNull  // or NeverNull, Random, RandomWeighted
}
val nullableResult = someWithNulls.some<String?>()  // will always be null

// Configure string generation
val someWithUuid = some {
    stringStrategy = StringStrategy.Uuid  // or Random, Readable
}
val uuid: String = someWithUuid.some<String>()  // will be UUID

// Configure collection sizes
val someWithLargeLists = some {
    collectionStrategy = CollectionStrategy(10..20)
}
val list: List<String> = someWithLargeLists.some<List<String>>()  // size 10-20

// Set a seed for reproducible results
val deterministic = some {
    seed = 12345L
}
val result1 = deterministic.some<User>()
val result2 = deterministic.some<User>()
// result1 and result2 are identical
```

### Custom Factories

Register custom factory functions for specific types:

```kotlin
val custom = some {
    register(String::class) { "custom-value-${random.nextInt(100)}" }
    register(Int::class) { 42 }
}
val result: MyDataClass = custom.some<MyDataClass>()
```

## Supported Types

### Primitives
- `String`, `Int`, `Long`, `Double`, `Float`, `Boolean`, `Char`, `Byte`, `Short`

### Standard Library
- `UUID` (Kotlin & Java)
- `BigDecimal`, `BigInteger`
- `LocalDate`, `LocalDateTime`
- `Instant` (Kotlin & Java)
- `Duration` (Kotlin & Java)

### Collections
- `List`, `MutableList`, `Set`, `MutableSet`
- `Map`, `MutableMap`
- Arrays (`Array<T>`, `IntArray`, etc.)

### Other
- Enums (random value)
- Sealed classes (random subclass)
- Object singletons
- Value classes
- Data classes with primary constructors
- Generic classes (e.g., `Wrapper<String>`, `Pair<Int, String>`)

## Requirements

- Kotlin 2.3.0+
- JVM 21+
- kotlin-reflect (included as transitive dependency)
