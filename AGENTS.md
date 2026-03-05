# AGENTS.md - Coding Agent Instructions for "some"

## Project Overview

"some" is a Kotlin test data generation library that creates random instances of data classes,
sealed classes/interfaces, collections, and primitive types. It uses a resolver chain pattern
where each TypeResolver handles specific types.

**Tech Stack:**
- Kotlin 2.3.0 (JVM)
- JDK 21
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

```
src/main/kotlin/dev/appoutlet/some/
├── Some.kt                    # Main API: some<T>(), someSetup {}
├── config/                    # Configuration classes
│   ├── SomeConfig.kt          # Main configuration, resolver registration
│   ├── CollectionStrategy.kt  # Collection size configuration
│   ├── NullableStrategy.kt    # Nullable field handling
│   └── StringStrategy.kt      # String generation strategies
├── core/                      # Core abstractions
│   ├── TypeResolver.kt        # Resolver interface
│   ├── ResolverChain.kt       # Resolution orchestration
│   └── FixtureContext.kt      # Context for custom factories
├── exception/                 # Custom exceptions (one per file)
│   ├── SomeCircularReferenceException.kt
│   └── SomeUnresolvableTypeException.kt
└── resolver/                  # Type resolvers (one per file)
    ├── IntResolver.kt
    ├── StringResolver.kt
    └── ... (30 resolvers total)

src/test/kotlin/dev/appoutlet/some/
├── config/                    # Config tests
├── integration/               # Integration tests (by category)
│   ├── DataClassIntegrationTest.kt
│   ├── CollectionsIntegrationTest.kt
│   ├── GenericsIntegrationTest.kt
│   ├── CustomFactoryIntegrationTest.kt
│   └── SetupIntegrationTest.kt
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

### Imports
- Group imports by: Kotlin stdlib, project imports, Java imports
- Use explicit imports, avoid wildcard imports
- Import `kotlin.reflect.typeOf` for type detection
- For experimental APIs, use `@OptIn` annotation (e.g., `@OptIn(ExperimentalUuidApi::class)`)

### Naming Conventions
- **Resolvers**: `{Type}Resolver` (e.g., `IntResolver`, `ListResolver`)
- **Kotlin vs Java types**: Prefix with language (e.g., `KotlinDurationResolver`, `JavaDurationResolver`)
- **Test classes**: `{Class}Test` (e.g., `IntResolverTest`)
- **Test methods**: Use backtick syntax with descriptive names
  ```kotlin
  @Test
  fun `IntResolver generates int values`() { ... }
  ```

### Type Detection Pattern
**Always use `typeOf<T>()` for exact type matching**, not `toString().contains()`:
```kotlin
// CORRECT
override fun canResolve(type: KType): Boolean = type == typeOf<Int>()

// WRONG - unreliable, causes false positives
override fun canResolve(type: KType): Boolean = type.toString().contains("Int")
```

### Resolver Implementation Pattern
Every resolver must implement `TypeResolver`:
```kotlin
class MyTypeResolver(private val random: Random) : TypeResolver {
    override fun canResolve(type: KType): Boolean = type == typeOf<MyType>()
    
    override fun resolve(type: KType, chain: ResolverChain): Any {
        return MyType(/* generated values */)
    }
}
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
- One exception class per file

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

### Known Limitations
- `Map::class == MutableMap::class` on JVM - use `type.toString().startsWith()` to distinguish
- Sealed classes/interfaces cannot be local (define at file level in tests)
- `kotlin.uuid.Uuid` requires `@OptIn(ExperimentalUuidApi::class)`

---

## Common Tasks

### Adding a New Resolver
1. Create `src/main/kotlin/.../resolver/MyTypeResolver.kt`
2. Implement `TypeResolver` interface with `canResolve` and `resolve`
3. Register in `SomeConfig.buildResolvers()` at appropriate position
4. Create `src/test/kotlin/.../resolver/MyTypeResolverTest.kt` with 3+ tests
5. Run `./gradlew test` to verify

### Fixing Type Detection Bugs
If a resolver matches wrong types, replace string-based detection with `typeOf<T>()`.

### Adding Kotlin/Java Dual Type Support
For types with both Kotlin and Java variants (Duration, Instant, UUID):
1. Create separate resolvers with `Kotlin` and `Java` prefixes
2. Register Kotlin resolver BEFORE Java resolver in SomeConfig
3. Add negative test to verify each rejects the other language's type
