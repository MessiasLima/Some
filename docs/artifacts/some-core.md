---
icon: lucide/box
---
# some-core

Use `dev.appoutlet:some-core` in Java and Kotlin/JVM projects.

This is the JVM artifact for Some's shared API, including `some<T>()`, `someSetup {}`, strategies, providers, custom factories, custom resolvers, and the standard supported types.

## Installation

=== "Gradle (Kotlin DSL)"

    ```kotlin
    testImplementation("dev.appoutlet:some-core:{version}")
    ```

=== "Gradle (Groovy)"

    ```groovy
    testImplementation 'dev.appoutlet:some-core:{version}'
    ```

## When to use it

- JVM test suites
- Kotlin/JVM libraries and applications
- Java projects that want the shared Some API

## Platform constraints

- JVM 17+
- Kotlin 2.3.0+

## Shared behavior

Most documented behavior in this site is shared across `some-core` and `some-android`.

- [Getting Started](../getting-started.md)
- [Strategies](../strategies/index.md)
- [Supported Types](../supported-types.md)
- [Type and Property Factories](../custom-factories.md)
- [Custom Resolvers](../custom-resolvers.md)
