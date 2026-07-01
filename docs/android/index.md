---
icon: lucide/smartphone
---

# Android

Use `dev.appoutlet:some-android` in Android projects when you want the shared Some API plus Android-specific type support.

`some-android` re-exports `some-core`, so Android projects should depend on `some-android` only.

## Installation

=== "Gradle (Kotlin DSL)"

    ```kotlin
    testImplementation("dev.appoutlet:some-android:{version}")
    ```

=== "Gradle (Groovy)"

    ```groovy
    testImplementation 'dev.appoutlet:some-android:{version}'
    ```

!!! note "Do not add both Android and core"

    `some-android` already includes the shared API from `some-core`.

## When to use it

- Android unit tests and Robolectric tests
- Android libraries and apps that need generated `android.net.Uri` values
- Projects that want one dependency for both the shared Some API and Android-specific support

## Supported Android types

All shared types from [Supported Types](../supported-types.md) are available here too.

Today, the Android module adds one Android-specific type:

| Type | Usage | Notes |
|------|-------|-------|
| `android.net.Uri` | `some<Uri>()` | See [Uri](uri.md) and [Uri Strategy](uri.md#uri-strategy) |

## Platform constraints

- Android `minSdk 24`
- Shared API behavior follows the same Kotlin/JVM constraints as `some-core`
