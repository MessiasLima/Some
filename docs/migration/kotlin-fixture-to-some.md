# Migrating from Appmattus KotlinFixture to Some

This guide explains how to migrate projects from Appmattus `kotlinfixture` to Some's compatibility module `dev.appoutlet:some-kotlin-fixture`.

The compatibility module allows you to replace dependencies and imports while keeping existing `kotlinFixture` configuration and call sites intact.

## 1. Dependency Replacement

Replace the Appmattus `fixture` dependency with `some-kotlin-fixture`:

=== "Gradle (Kotlin DSL)"

    ```kotlin
    dependencies {
        // Remove:
        // implementation("com.appmattus.fixture:fixture:1.2.0")

        // Add:
        implementation("dev.appoutlet:some-kotlin-fixture:0.4.0")
    }
    ```

=== "Gradle (Groovy DSL)"

    ```groovy
    dependencies {
        // Remove:
        // implementation 'com.appmattus.fixture:fixture:1.2.0'

        // Add:
        implementation 'dev.appoutlet:some-kotlin-fixture:0.4.0'
    }
    ```

=== "Maven"

    ```xml
    <dependency>
        <groupId>dev.appoutlet</groupId>
        <artifactId>some-kotlin-fixture</artifactId>
        <version>0.4.0</version>
    </dependency>
    ```

## 2. Import Replacement

Update wildcard imports in your test files:

```kotlin
// Remove:
import com.appmattus.kotlinfixture.kotlinFixture

// Replace with:
import dev.appoutlet.some.compat.kotlinfixture.*
```

## 3. Supported Features and API Usage

### Creating a Fixture

```kotlin
val fixture = kotlinFixture()

val user: User = fixture()
val age: Int = fixture(18..65)
val name: String = fixture(listOf("Alice", "Bob", "Charlie"))
```

### Derived Fixtures (`Fixture.new`)

Derived fixtures inherit parent configuration without mutating the parent instance:

```kotlin
val baseFixture = kotlinFixture {
    factory<String> { "base" }
}

val derivedFixture = baseFixture.new {
    factory<Int> { 42 }
}
```

### Sequences (`SequenceStrategy`)

Generate sequences lazily:

```kotlin
// Unbounded sequence
val infiniteStream = fixture.asSequence<User>(SequenceStrategy.Unbounded)

// Bounded sequence
val batch = fixture.asSequence<User>(SequenceStrategy.Bounded(10)).toList()
```

### Type Factories

Override type resolution by exact `KType`:

```kotlin
val fixture = kotlinFixture {
    factory<String> { "fixed-value" }
    factory<User> { User(id = range(1..100), name = fixture()) }
}
```

### Property Factories

Override constructor parameter values:

```kotlin
val fixture = kotlinFixture {
    property(User::name) { "OverriddenName" }
    property(User::age) { range(18..30) }
}
```

### Subtype Mapping

Map superclasses or interfaces to concrete implementations:

```kotlin
val fixture = kotlinFixture {
    subType<Number, Int>()
    subType<Animal, Dog>()
}
```

### Filters

Apply predicates and distinct rules to generated values:

```kotlin
val fixture = kotlinFixture {
    filter<Int> {
        filter { it % 2 == 0 }
    }

    filter<String> {
        distinct()
    }
}
```

### Configured Random Source

Use a deterministic seed for reproducible data:

```kotlin
val fixture = kotlinFixture {
    random = Random(12345L)
}
```

### Strategy Aliases

Configure nullability, default constructor parameters, and recursion behavior:

```kotlin
val fixture = kotlinFixture {
    nullabilityStrategy = NullabilityStrategy.NeverNullStrategy
    optionalStrategy = OptionalStrategy.AlwaysOptionalStrategy
    recursionStrategy = RecursionStrategy.NullRecursionStrategy
}
```

## 4. Known Compatibility Limitations and Deferred Scope

The following features are currently deferred or unsupported in this phase:

- **Collection size strategies** (`CollectionStrategy` and `repeatCount`) are deferred to future releases.
- **Appmattus ecosystem plugins** (`fixture-javafaker`, `fixture-generex`, `fixture-kotest`, and Android compatibility modules) are not included in this compatibility module.
- **Deprecated Appmattus `create(...)` APIs** are omitted; use `fixture<T>()` or `fixture(range)` instead.
- **Property factories** support constructor-declared properties handled by Some's `ClassResolver`. Private non-constructor properties or Java setters will throw a clear exception.
