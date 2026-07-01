# AGENTS.md - Coding Agent Instructions for "some"

## Project Overview

"some" is a Kotlin test data generation library that creates random instances of data classes,
sealed classes/interfaces, collections, and primitive types. It uses a resolver chain pattern
where each Resolver handles specific types.

**Tech Stack:**
- Kotlin (JVM)
- Gradle (Kotlin DSL)
- JUnit 5 (via kotlin-test)
- kotlin-reflect for runtime type introspection

---

## Useful Commands

```bash
# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "*IntResolverTest"

# Run a single test method (use full method name with backticks escaped)
./gradlew test --tests "*IntResolverTest.IntResolver generates int values"

# Run tests in a specific package
./gradlew test --tests "dev.appoutlet.some.resolver.*"

# Run tests with fresh execution (ignore cache)
./gradlew test --rerun-tasks
```

---

## Project Structure

The project is split into Gradle modules:

- `core` — Main library (`some-core`)
- `android` — Android integration (`some-android`)
- `kotest` — Kotest property-based testing integration (`some-kotest`)

Published artifacts are intended to be used as follows:

- `some-core` — Base `some` / `someSetup` API for Java and Kotlin/JVM projects
- `some-android` — Android artifact that re-exports the core API, so consumers should not add `some-core` separately
- `some-kotest` — Kotest `Arb` integration that should be added alongside either `some-core` or `some-android`

Documentation pages are under `docs/` and the site config is in `zensical.toml`.
---

## Code Style Guidelines

### File Organization
- **One class per file** - Each resolver, exception, and config class has its own file
- **File name matches class name** - `IntResolver.kt` contains `class IntResolver`
- **Package structure mirrors functionality** - resolvers in `resolver/`, config in `config/`

### Naming Conventions
- **Resolvers**: `{Type}Resolver` (e.g., `IntResolver`, `ListResolver`)
- **Kotlin vs Java types**: Prefix with language (e.g., `KotlinDurationResolver`, `JavaDurationResolver`)

### Strategy Registration Pattern
Strategies are registered via `SomeConfigBuilder.strategy()` (for strategies) and `SomeConfigBuilder.factory()` (for type factories):

```kotlin
someSetup {
    // Strategy registration - overrides generation behavior
    strategy(NullableStrategy.NeverNull)
    strategy(StringStrategy.Uuid)
    strategy(CollectionStrategy(5..10))

    // Factory registration - overrides type resolution
    factory(String::class) { "fixed-value" }
}
```

Resolvers receive only the strategy they need (nullable, falling back to the strategy default).
Custom factories access strategies through `FixtureContext.strategyProvider` using the idiomatic `get()` extension:

```kotlin
factory(MyType::class) {
    val strategy = strategyProvider.get<StringStrategy>()
    MyType(strategy is StringStrategy.Readable)
}
```

### Type Detection Pattern
**Always use `typeOf<T>()` for exact type matching**, not `toString().contains()`:
```kotlin
// CORRECT
override fun canResolve(type: KType): Boolean = type == typeOf<Int>()

// WRONG - unreliable, causes false positives
override fun canResolve(type: KType): Boolean = type.toString().contains("Int")
```

### Test Pattern for Resolvers
Each resolver should have 3+ tests:
```kotlin
class MyTypeResolverTest {
    @Test
    fun `MyTypeResolver generates MyType values`() {
        val resolver = MyTypeResolver(MyStrategy(), Random.Default)
        val result = resolver.resolve(typeOf<MyType>(), defaultTestChain)
        assertIs<MyType>(result)
    }
    
    @Test
    fun `MyTypeResolver canResolve detects MyType type`() {
        val resolver = MyTypeResolver(random = Random.Default)
        assertTrue(resolver.canResolve(typeOf<MyType>()))
    }
    
    @Test
    fun `MyTypeResolver rejects non-MyType types`() {
        val resolver = MyTypeResolver(random = Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
    }
}
```

### Error Handling
- Use custom exceptions in `dev.appoutlet.some.exception` package
- Exception classes extend `Exception` with descriptive messages

### Resolver Registration Order (in SomeConfig)
Order matters - first match wins:
1. CustomTypeFactoryResolver (user overrides)
2. NullableResolver
3. ObjectResolver, EnumResolver, SealedClassResolver, ValueClassResolver
4. Primitive resolvers (String, Int, Long, etc.)
5. **Kotlin native types FIRST** (KotlinUuidResolver, KotlinInstantResolver, KotlinDurationResolver)
6. **Java types SECOND** (JavaUuidResolver, JavaInstantResolver, JavaDurationResolver, JavaZonedDateTimeResolver)
7. Collection resolvers (List, Set, Map, Array)
8. ClassResolver (fallback for classes with constructors)
