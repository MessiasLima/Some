---
icon: lucide/link
---

# Uri

`some-android` includes a resolver for `android.net.Uri`.

Once `some-android` is on your test classpath, `some<Uri>()` works without custom factories or custom resolvers.

!!! note "Android-only feature"

    `UriStrategy` is part of `some-android`. It is not available from `some-core`.

## Basic usage

```kotlin
import android.net.Uri
import dev.appoutlet.some.some

val uri: Uri = some()
```

The generated value is a parseable `Uri` with a scheme, host, and path. Depending on the active strategy, it may also include a query string or fragment.

## Uri strategy

Use `UriStrategy` when the URI scheme matters for the code you are testing.

| Strategy | Default | What it generates | Good for |
|----------|---------|-------------------|----------|
| `UriStrategy.Random` | Yes | Randomly chooses `content://`, `file://`, or `https://` | General tests that only need a valid `Uri` |
| `UriStrategy.Content` | No | Only `content://...` URIs | `ContentResolver`, providers, and content URIs |
| `UriStrategy.File` | No | Only `file://...` URIs | File URI handling |
| `UriStrategy.Url` | No | Only `https://...` URIs with a domain-like host | Web links and URL-style inputs |

### Configure it per call

```kotlin
import android.net.Uri
import dev.appoutlet.some.android.strategy.UriStrategy
import dev.appoutlet.some.some

val uri = some<Uri> {
    strategy(UriStrategy.Url)
}
```

### Configure it once and reuse it

```kotlin
import android.net.Uri
import dev.appoutlet.some.android.strategy.UriStrategy
import dev.appoutlet.some.someSetup

val some = someSetup {
    strategy(UriStrategy.Content)
}

val avatarUri: Uri = some()
val documentUri: Uri = some()
```

## What the resolver generates

- A scheme controlled by `UriStrategy`
- A non-empty host
- At least one path segment
- An optional query string
- An optional fragment

When you use `UriStrategy.Url`, the host is generated in a domain-like format such as `example.ab`.

## Picking the right strategy

- Use `UriStrategy.Random` when your test only needs a valid `Uri`
- Use `UriStrategy.Content` when your code branches on `content` URIs
- Use `UriStrategy.File` when your code expects file URIs
- Use `UriStrategy.Url` when your code validates or handles `https` links

## Examples

=== "Random (default)"

    ```kotlin
    val uri: Uri = some()
    // Could be content://..., file://..., or https://...
    ```

=== "Content"

    ```kotlin
    val uri = some<Uri> {
        strategy(UriStrategy.Content)
    }
    // content://media/images
    ```

=== "File"

    ```kotlin
    val uri = some<Uri> {
        strategy(UriStrategy.File)
    }
    // file://downloads/report
    ```

=== "Url"

    ```kotlin
    val uri = some<Uri> {
        strategy(UriStrategy.Url)
    }
    // https://example.io/profile?id=42
    ```

If your test cares about the scheme, prefer an explicit strategy instead of asserting against the default random output.

See [Android](index.md) for installation and the list of Android-specific supported types.
