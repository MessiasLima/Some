# Module Some

## Overview

**Some** is a Kotlin JVM library that generates populated instances of any Kotlin class for testing purposes, with zero configuration required. It creates random instances of data classes, sealed classes/interfaces, collections, and primitive types using a resolver chain pattern where each `TypeResolver` handles specific types.

## Key Features

- **Zero-config generation**: Call `some<T>()` and get a fully populated instance of `T`
- **Reproducible results**: Set a seed for deterministic output
- **Configurable behavior**: Control nullable fields, string formats, and collection sizes
- **Custom factories**: Register type-specific factory functions to override default generation
- **Generic support**: Handles generic types (e.g., `Wrapper<String>`, `Pair<Int, String>`)

## Usage

### Basic Generation

```kotlin
import dev.appoutlet.some.some

data class User(val name: String, val age: Int)

val user: User = some<User>()
val users: List<User> = some<List<User>>()
```

### Configuration

```kotlin
val configured = some {
    nullableStrategy = NullableStrategy.NeverNull
    stringStrategy = StringStrategy.Uuid
    collectionStrategy = CollectionStrategy(10..20)
    seed = 12345L
    register(String::class) { "custom-${random.nextInt(100)}" }
}

val result = configured.some<MyDataClass>()
```

## Supported Types

| Category | Types |
|---|---|
| Primitives | `String`, `Int`, `Long`, `Double`, `Float`, `Boolean`, `Char`, `Byte`, `Short` |
| Standard Library | `UUID` (Kotlin & Java), `BigDecimal`, `BigInteger`, `LocalDate`, `LocalDateTime`, `Instant` (Kotlin & Java), `Duration` (Kotlin & Java) |
| Collections | `List`, `MutableList`, `Set`, `MutableSet`, `Map`, `MutableMap`, `Array<T>`, `IntArray`, etc. |
| Other | Enums, sealed classes, object singletons, value classes, data classes, generic classes |

## Architecture

The library uses a **resolver chain** pattern. Each `TypeResolver` declares whether it `canResolve()` a given `KType` and, if so, `resolve()` produces a random instance. Resolvers are tried in registration order — first match wins:

| Priority | Resolver |
|---|---|
| 1 | CustomFactoryResolver (user overrides) |
| 2 | NullableResolver |
| 3 | ObjectResolver, EnumResolver, SealedClassResolver, ValueClassResolver |
| 4 | Primitive resolvers (String, Int, Long, etc.) |
| 5 | Kotlin native types (Uuid, Instant, Duration) |
| 6 | Java types (Uuid, Instant, Duration) |
| 7 | Collection resolvers (List, Set, Map, Array) |
| 8 | DataClassResolver (fallback for data classes) |

Type detection uses `typeOf<T>()` for exact matching rather than string-based heuristics to avoid false positives.

## Requirements

- Kotlin 2.3.0+
- JVM 21+
- `kotlin-reflect` (included as transitive dependency)

## Project Status

Early development stage — not yet ready for production use. APIs are subject to change.
