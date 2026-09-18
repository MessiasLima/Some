# Migrating from Appmattus KotlinFixture to Some

`some-kotlin-fixture` provides a small compatibility API for projects migrating from Appmattus KotlinFixture. It keeps the `kotlinFixture()` entry point and fixture invocation style, while delegating configuration and generation to Some's `SomeConfigBuilder`.

The compatibility module is intentionally thin. Configure factories, properties, strategies, and seeds with the core Some API rather than the removed KotlinFixture-specific configuration types.

## Dependency

Replace the Appmattus dependency with `some-kotlin-fixture`:

=== "Gradle Kotlin DSL"

    ```kotlin
    dependencies {
        // Remove:
        // implementation("com.appmattus.fixture:fixture:1.2.0")

        implementation("dev.appoutlet:some-kotlin-fixture:0.4.0")
    }
    ```

=== "Gradle Groovy DSL"

    ```groovy
    dependencies {
        // Remove:
        // implementation 'com.appmattus.fixture:fixture:1.2.0'

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

## Imports

Replace the Appmattus import with the compatibility entry point and sequence strategy:

```kotlin
// Remove:
import com.appmattus.kotlinfixture.kotlinFixture

// Add:
import dev.appoutlet.some.compat.kotlinfixture.SequenceStrategy
import dev.appoutlet.some.compat.kotlinfixture.kotlinFixture
```

Some configuration types come from the core module:

```kotlin
import dev.appoutlet.some.config.DefaultValueStrategy
import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.config.StringStrategy
```

## Basic Generation

The fixture remains callable and supports selecting from a non-empty range:

```kotlin
val fixture = kotlinFixture()

val user: User = fixture()
val age: Int = fixture(18..65)
val name: String = fixture(listOf("Alice", "Bob", "Charlie"))
```

An empty range falls back to generated data.

## Configuration

The configuration lambda is a `SomeConfigBuilder` lambda. Type factories receive a `KClass` explicitly:

```kotlin
val fixture = kotlinFixture {
    factory(String::class) { "fixed-value" }
    factory(User::class) {
        User(
            id = random.nextInt(1, 101),
            name = "generated-by-factory"
        )
    }
}
```

Factory lambdas receive a `FixtureContext`. It exposes `random`, `resolutionStack`, and `strategyProvider`.

Property factories use a property reference and override constructor property values:

```kotlin
val fixture = kotlinFixture {
    property(User::name) { "OverriddenName" }
    property(User::age) { 30 }
}
```

## Per-Call Overrides

Pass a builder lambda to an individual fixture invocation. The override applies only to that call:

```kotlin
val fixture = kotlinFixture {
    factory(String::class) { "default" }
}

val overridden: String = fixture {
    factory(String::class) { "override" }
}

val unchanged: String = fixture()
```

`Fixture.create` also accepts a one-off builder lambda:

```kotlin
val value: Int = fixture.create {
    factory(Int::class) { 42 }
}
```

## New Fixtures

`Fixture.new` creates a new fixture from the supplied builder lambda:

```kotlin
val base = kotlinFixture {
    factory(String::class) { "base" }
}

val other = base.new {
    factory(Int::class) { 42 }
}
```

The new fixture uses the configuration passed to `new`; it does not merge the original fixture's configuration.

## Sequences

Use `SequenceStrategy.Bounded` for a finite sequence or `SequenceStrategy.Unbounded` for a lazy infinite sequence:

```kotlin
val batch = fixture
    .asSequence<User>(SequenceStrategy.Bounded(10))
    .toList()

val infinite = fixture
    .asSequence<User>(SequenceStrategy.Unbounded)
    .take(10)
    .toList()
```

`SequenceStrategy.Bounded(0)` produces an empty sequence. Negative bounds are rejected.

## Seeds and Strategies

Use `seed` for reproducible generation:

```kotlin
val fixture = kotlinFixture {
    seed = 12345L
}
```

Register Some strategies directly with `strategy`:

```kotlin
val fixture = kotlinFixture {
    strategy(StringStrategy.Readable)
    strategy(NullableStrategy.NeverNull)
    strategy(DefaultValueStrategy.Generate)
}
```

## Unsupported KotlinFixture APIs

The simplified compatibility module does not provide the former KotlinFixture-specific APIs:

- `Configuration` and `ConfigurationBuilder`
- `NullabilityStrategy`, `OptionalStrategy`, and `RecursionStrategy` aliases
- `filter` and distinct-value configuration
- `subType` mappings
- the `range` helper inside factories
- KotlinFixture plugin integrations such as Java Faker, Generex, Kotest, and Android

Use the corresponding core Some API where one exists. For unsupported behavior, configure a `factory` or generate the value directly in a test-specific helper.
