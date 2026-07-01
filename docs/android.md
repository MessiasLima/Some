---
icon: lucide/smartphone
---
# Android apps

Use `dev.appoutlet:some-android` in Android projects.

`some-android` re-exports the shared Some API from `some-core`, so Android users should depend on `some-android` and should not add `some-core` separately.

## Installation

=== "Gradle (Kotlin DSL)"

    ```kotlin
    testImplementation("dev.appoutlet:some-android:{version}")
    ```

=== "Gradle (Groovy)"

    ```groovy
    testImplementation 'dev.appoutlet:some-android:{version}'
    ```

## When to use it

- Android unit tests
- Android libraries and applications that want the shared Some API

## Platform constraints

- Android `minSdk 24`
- Shared API behavior follows the same Kotlin/JVM constraints as `some-core`

## Artifact-specific behavior

- Re-exports the shared API from `some-core`
- Consumers should not add both `some-android` and `some-core`
- Does not currently add Android-only strategies or supported types beyond the shared API

## Shared behavior

The main Some documentation applies here as well:

- [Getting Started](getting-started.md)
- [Strategies](strategies/index.md)
- [Supported Types](supported-types.md)
- [Type and Property Factories](custom-factories.md)
- [Custom Resolvers](custom-resolvers.md)
