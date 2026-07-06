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
- Android libraries and apps that need generated `Uri`, `Rect`, `Point`, `Size`, or similar Android framework values
- Projects that want one dependency for both the shared Some API and Android-specific support

## Supported Android types

All shared types from [Supported Types](../supported-types.md) are available here too.

Today, the Android module adds these Android-specific types:

| Type | Usage            | Notes |
|------|------------------|-------|
| `android.net.Uri` | `some<Uri>()`    | See [Uri](uri.md) and [Uri Strategy](uri.md#uri-strategy) |
| `android.graphics.Rect` | `some<Rect>()`   | Random integer bounds with `left < right` and `top < bottom` |
| `android.graphics.RectF` | `some<RectF>()`  | Random float bounds with `left < right` and `top < bottom` |
| `android.graphics.Point` | `some<Point>()`  | Random integer `x` and `y` |
| `android.graphics.PointF` | `some<PointF>()` | Random float `x` and `y` |
| `android.util.Size` | `some<Size>()`   | Random positive integer `width` and `height` |
| `android.util.SizeF` | `some<SizeF>()`  | Random positive float `width` and `height` |
| `android.util.Pair<F, S>` | `some<Pair<String, Int>>()` | Resolves `first` and `second` by delegating both generic type arguments through the resolver chain |
| `android.os.Bundle` | `some<Bundle>()` | One random value each for `String`, `Int`, `Long`, `Float`, `Double`, and `Boolean` |
| `android.os.UserHandle` | `some<UserHandle>()` | API 24+; generated from a random non-system UID |
| `android.graphics.Color` | `some<Color>()` | API 26+; generated from random RGB channel values |

## Compose support

`some-android` includes specialized support for Jetpack Compose types, including units and geometry.

See [Compose](compose.md) for the full list of supported Compose types.

## Platform constraints

- Android `minSdk 26`
- Shared API behavior follows the same Kotlin/JVM constraints as `some-core`
