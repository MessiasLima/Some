---
icon: lucide/package
---

# Bundle

`some-android` includes `BundleResolver` for generating `android.os.Bundle` instances with random entries.

## Generated bundles

`BundleResolver` creates a new `Bundle()` and populates it with:

- 0 to 5 entries
- Non-empty string keys
- Values of type `String`, `Int`, `Long`, `Float`, `Double`, or `Boolean`

Entry count, keys, and value types are all chosen randomly.

## Direct usage

```kotlin
import android.os.Bundle
import dev.appoutlet.some.android.resolver.BundleResolver
import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.typeOf

val resolver = BundleResolver(Random.Default)
val chain = ResolverChain(emptyList(), NullableStrategy.NullOnCircularReference)
val bundle = resolver.resolve(typeOf<Bundle>(), chain) as Bundle
```

## Value types

Each entry uses one of the supported `Bundle` primitive methods:

- `putString`
- `putInt`
- `putLong`
- `putFloat`
- `putDouble`
- `putBoolean`

See [Android](index.md) for installation and the list of Android-specific supported types.
