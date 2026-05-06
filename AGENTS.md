# AGENTS.md - Coding Agent Instructions for "some"

## Project Overview

"some" is a Kotlin test data generation library that creates random instances of data classes,
sealed classes/interfaces, collections, and primitive types. It uses a resolver chain pattern
where each TypeResolver handles specific types.

**Tech Stack:**
- Kotlin (JVM)
- Gradle (Kotlin DSL)
- JUnit 5 (via kotlin-test)
- kotlin-reflect for runtime type introspection

---

## Build Commands

```bash
# Build the project
./gradlew build

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

# Clean and rebuild
./gradlew clean build

# Check compilation without running tests
./gradlew compileKotlin compileTestKotlin
```

---

## Project Structure

Documentation pages are under `docs/` and the site config is in `zensical.toml`.

```
src/main/kotlin/dev/appoutlet/some/
├── Some.kt                    # Main API: some<T>(), someSetup {}
├── config/                    # Configuration classes
├── core/                      # Core abstractions
├── exception/                 # Custom exceptions (one per file)
└── resolver/                  # Type resolvers

src/test/kotlin/dev/appoutlet/some/
├── config/                    # Config tests
├── integration/               # Integration tests (by category)
├── resolver/                  # Resolver unit tests (one per resolver)
└── test/
    └── TestHelpers.kt         # Shared test utilities
```

---

## Code Style Guidelines

### File Organization
- **One class per file** - Each resolver, exception, and config class has its own file
- **File name matches class name** - `IntResolver.kt` contains `class IntResolver`
- **Package structure mirrors functionality** - resolvers in `resolver/`, config in `config/`

### Naming Conventions
- **Resolvers**: `{Type}Resolver` (e.g., `IntResolver`, `ListResolver`)
- **Kotlin vs Java types**: Prefix with language (e.g., `KotlinDurationResolver`, `JavaDurationResolver`)

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
        val resolver = MyTypeResolver(Random.Default)
        val result = resolver.resolve(typeOf<MyType>(), defaultTestChain)
        assertIs<MyType>(result)
    }
    
    @Test
    fun `MyTypeResolver canResolve detects MyType type`() {
        assertTrue(resolver.canResolve(typeOf<MyType>()))
    }
    
    @Test
    fun `MyTypeResolver rejects non-MyType types`() {
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
1. CustomFactoryResolver (user overrides)
2. NullableResolver
3. ObjectResolver, EnumResolver, SealedClassResolver, ValueClassResolver
4. Primitive resolvers (String, Int, Long, etc.)
5. **Kotlin native types FIRST** (KotlinUuidResolver, KotlinInstantResolver, KotlinDurationResolver)
6. **Java types SECOND** (JavaUuidResolver, JavaInstantResolver, JavaDurationResolver)
7. Collection resolvers (List, Set, Map, Array)
8. DataClassResolver (fallback for data classes)

