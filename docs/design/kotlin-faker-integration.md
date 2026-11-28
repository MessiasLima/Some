# Spike: kotlin-faker integration with annotation-driven customization

Status: research complete, implementation not started.

This document records the investigation for [issue #33](https://github.com/MessiasLima/Some/issues/33): whether
Some can integrate [kotlin-faker](https://serpro69.github.io/kotlin-faker/) so data-class constructor parameters can
be annotated and populated with realistic fake values.

## Recommendation

The integration is feasible, but it should live in a separate optional module (`some-faker`) rather than in
`some-core`. Use runtime annotation scanning for the first version, and keep a small extension point in the core
constructor path so the module can plug into `ClassResolver` without duplicating its constructor logic.

Recommended shape:

1. Add a generic parameter-annotation handler to `ClassResolver`/`SomeConfigBuilder` in core.
2. Publish `some-faker` with a `@SomeFaker("...")` annotation, Faker configuration, path resolution, and errors.
3. Resolve annotation paths through a cached, explicit `Faker` provider lookup rather than a full dynamic DSL.
4. Keep KSP out of the first version. It is a reasonable future optimization, not a prerequisite.

Estimated effort: **medium**. Most of the work is the core parameter-handler extension, the optional module, cache
and error handling, and documentation. The Faker call itself is small.

## Findings

### Runtime annotation scanning from `KParameter`

Verified with a throwaway kotlin-reflect test against the project's Kotlin version:

- A runtime-retained annotation with `AnnotationTarget.VALUE_PARAMETER` is readable from
  `KClass.primaryConstructor.parameters[i].annotations`.
- The same lookup works for a value class constructor parameter.
- The same lookup works for a generic constructor parameter such as `data class Box<T>(@SomeFaker("...") val value: T)`.

So runtime scanning is reliable for the cases Some needs. Two limits should be documented:

- Use `AnnotationTarget.VALUE_PARAMETER`, not Java `TYPE_USE`, because Some reads constructor parameters rather than
  property type uses.
- Annotations attached to a property rather than the constructor parameter may not be visible from `KParameter` in
  every declaration form. The module should read constructor parameters and document that placement.

### Path expressions

`"name.fullName"` can be resolved with reflection:

1. Read the `Faker` instance's `name` provider property.
2. Find the zero-argument `fullName()` function on that provider.
3. Invoke it and check that the result is assignable to the constructor parameter type.

This is workable for zero-argument providers, which cover most useful cases (`name.fullName`, `internet.emailAddress`,
`address.city`, `phoneNumber.phoneNumber`, and so on). Reflection is brittle for overloads, functions with arguments,
and provider renames, so the recommended API is a hybrid:

- `@SomeFaker("name.fullName")` for the common zero-argument path.
- An escape hatch such as `@SomeFakerUsing(MyGenerator::class)` for complex values, where `MyGenerator` implements a
  small `FakerValueGenerator` interface and receives the `Faker` instance.

A full custom expression language (arguments, formatting, random choices) is unnecessary for the first version and
would make the annotation contract harder to validate.

### Dependency footprint and compatibility

Latest published version at investigation time: `io.github.serpro69:kotlin-faker` `2.0.0-rc.13` (April 2026
metadata). It is still a release candidate, which is a risk to call out in the module documentation.

Approximate JVM artifact sizes:

| Artifact | Size |
|----------|------|
| `kotlin-faker-jvm` | 3.2 MB |
| `icu4j` (transitive) | 14.0 MB |
| `jackson-databind` + `jackson-module-kotlin` (transitive) | 1.8 MB |
| `rgxgen` (transitive) | 0.2 MB |

The total is roughly 19 MB before the Kotlin standard library and reflection, which Some already uses. That is too
large to add to `some-core` by default. A separate `some-faker` artifact keeps the base library lean and lets users
opt in.

The artifact is published with Kotlin `2.1.21` metadata. Kotlin libraries are generally forward-compatible, so it
should run with the project's Kotlin `2.4.x`, but the module build should align or override the Kotlin stdlib/reflect
versions and test on the supported Kotlin version. Pin the Faker version explicitly; do not inherit a floating
pre-release.

Locale and seed are already first-class in Faker:

```kotlin
val faker = Faker(fakerConfig {
    locale = "nb-NO"
    randomSeed = 42
})
```

### Runtime scanning versus KSP

Runtime scanning is recommended for the first version:

- It matches the existing resolver/reflection architecture and needs no consumer build-plugin changes.
- The reflection cost is one `parameters`/`annotations` read per constructor, which can be cached per class.
- KSP would add an annotation processor module, consumer plugin configuration, and a compile-time/runtime split for a
  test-data library whose main value is convenience.

KSP becomes attractive if the project later wants compile-time validation of annotation paths, generated metadata, or
zero-reflection startup. It is not needed for a useful first release.

## Resolver placement

A normal type `Resolver` cannot implement this feature because annotations belong to constructor parameters, not to the
requested type. A `FakerResolver` that claims `String` would affect every `String`, not only annotated properties.

The integration point belongs in `ClassResolver`, where constructor parameters are already inspected. The recommended
order for each parameter is:

1. Existing property factory (`property(User::email) { ... }`), because it is the most explicit override.
2. A registered parameter-annotation handler (the Faker module).
3. Normal type resolution through the chain.

The class itself is still resolved normally, so type factories and subtype mappings continue to take precedence over
everything that happens inside the constructor.

Suggested core API:

```kotlin
fun interface ParameterFactory {
    fun create(parameter: KParameter, context: FixtureContext): Any?
}

fun <A : Annotation> SomeConfigBuilder.parameterFactory(
    annotation: KClass<A>,
    factory: ParameterFactory,
)
```

`ClassResolver` checks annotations before generating the parameter. `some-faker` registers a `ParameterFactory` for
`SomeFaker` and `SomeFakerUsing`. This keeps the Faker dependency out of core and keeps the core extension generic.

An alternative would be to add a `currentParameter` field to `FixtureContext` and let factories read it, but that
makes every factory context wider and does not solve matching by annotation type. A dedicated parameter factory is
cleaner.

## Configuration integration

Store Faker configuration as a regular strategy so it flows through `SomeConfig`, `toBuilder`, per-call overrides, and
`FixtureContext.strategyProvider` without adding Faker types to core:

```kotlin
// some-faker
class FakerConfiguration(
    val locale: String = "en",
    val seed: Long? = null,
) : Strategy {
    override val key: KClass<out Strategy> = FakerConfiguration::class
}

fun SomeConfigBuilder.faker(block: FakerConfigurationBuilder.() -> Unit) {
    strategy(FakerConfigurationBuilder().apply(block).build())
}
```

Usage:

```kotlin
val some = someSetup {
    seed = 42
    faker {
        locale = "en_US"
    }
}

data class User(
    @SomeFaker("name.fullName") val name: String,
    @SomeFaker("internet.emailAddress") val email: String,
    @SomeFaker("address.city") val city: String,
)

val user = some<User>()
```

The Faker instance should be created from the configuration and cached with the generated resolver/parameter-factory
state. If Faker's random source is seeded independently, use the same `seed` as Some so separate runs remain
deterministic. If the shared `Random` is passed in, document that call order affects both generators.

Thread safety needs a follow-up test. Faker instances are not documented as thread-safe, and Some resolvers are reused
across `some()` calls. The safest first implementation is one Faker per `Some` instance plus thread-confinement
documentation, or a `ThreadLocal<Faker>` if concurrent use of one `Some` instance must be supported.

## Annotation API

Proposed annotation surface:

```kotlin
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class SomeFaker(val value: String)

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class SomeFakerUsing(val generator: KClass<out FakerValueGenerator>)
```

Rules:

- One annotation per constructor parameter.
- `SomeFaker` paths are dot-separated provider/function paths for zero-argument functions.
- The generated value must be assignable to the parameter type. A mismatch throws a clear `SomeFakerException` naming
  the class, parameter, path, and expected/actual types.
- `SomeFakerUsing` covers complex values and custom generators.
- Nullable parameters can still receive `null` according to the parameter declaration; Faker values are used when a
  value is generated.
- Annotations on value-class constructor parameters work; annotations on arbitrary property type uses are out of scope.

## Proposed implementation breakdown

1. **Core: parameter annotation handlers** (medium)
   - Add `ParameterFactory` and registration to `SomeConfig`/`SomeConfigBuilder`.
   - Hook it into `ClassResolver` before normal parameter resolution.
   - Tests for precedence, annotation matching, and generated value assignment.
2. **`some-faker` module** (medium)
   - Add module and dependency.
   - Add `SomeFaker`, `SomeFakerUsing`, `FakerConfiguration`, `SomeConfigBuilder.faker { ... }`.
   - Implement cached path resolution and the `FakerValueGenerator` escape hatch.
   - Add clear errors and tests for locale, seed, path failures, type mismatches, and generics.
3. **Documentation** (small)
   - Add a `some-faker` page with installation, annotation examples, configuration, limitations, and thread-safety notes.
   - Link it from the main docs navigation.

## Risks and open questions

- `kotlin-faker` `2.0.0-rc.13` is a pre-release. Pin it and be prepared for API changes before 2.0 final.
- The transitive `icu4j` dependency is large; keep the module optional and document the footprint.
- Reflection path resolution is brittle if Faker renames providers or changes function signatures. Cache lookups and
  fail with actionable errors.
- Faker thread safety is undocumented. Verify before advertising concurrent use of a single `Some` instance.
- Locale coverage varies by provider; Faker falls back to `en` when a localized dictionary lacks a key.
- Value-class and generic-parameter annotations work for constructor parameters in the tested Kotlin version, but
  should remain covered by regression tests when the module is implemented.

## Alternatives considered

- **Put Faker in `some-core`**: simplest dependency graph, but forces a ~19 MB optional dependency on every user. Rejected.
- **KSP processor**: compile-time validation and metadata, but adds build complexity for consumers and a second
  artifact. Deferred.
- **Extend `ClassResolver` directly with Faker logic**: would couple core to Faker and make the dependency mandatory.
  Rejected.
- **Custom resolver that claims annotated types**: cannot see parameter annotations and would affect all values of a
  type. Rejected.
- **Expression language for Faker paths**: powerful, but unnecessary for the common zero-argument cases. Start with
  paths plus an explicit generator escape hatch.
