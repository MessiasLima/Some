---
icon: lucide/puzzle
---
# Custom Resolvers

Some discovers custom `Resolver` implementations at runtime through Java's `ServiceLoader` mechanism. This lets you extend Some with support for domain-specific, third-party, or internal application types **without requiring every consumer to write configuration code**.

If you only need to override how a single type is generated in your own tests, a [custom factory](custom-factories.md) is simpler. Use a custom resolver when you are **building a library** that others will depend on, **adding support for internal types in your own application**, or when the type needs its own resolution logic that delegates to the resolver chain.

## How it works

Some defines a service-provider interface called `ResolverProvider`. When fixture generation starts, Some calls `ServiceLoader.load(ResolverProvider::class.java)` to discover all implementations on the classpath. Each provider returns a list of [Resolver] instances that are inserted into the resolver chain.

The resolver chain order is:

1. **Custom type factories** — explicit user factories registered with `factory()`.
2. **Nullable resolver** — handles nullable wrappers before concrete types.
3. **Discovered resolvers** — contributed by `ResolverProvider` implementations.
4. **Built-in resolvers** — standard types like `String`, `Int`, `List`, etc.
5. **Class resolver** — fallback for data classes and other constructable types.

Because discovered resolvers sit between nullable handling and built-in resolvers, a third-party resolver for `String` would take precedence over the built-in `StringResolver`, while still allowing a user-registered type factory to override everything.

Misbehaving providers are silently skipped. If a provider throws during discovery or resolver creation, Some continues with the remaining providers and built-in chain.

## Implementing a ResolverProvider

### 1. Create a resolver

A resolver implements the `Resolver` interface with two methods:

```kotlin
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.Resolver
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class UrlResolver : Resolver {

    override fun canResolve(type: KType): Boolean = type == typeOf<java.net.URL>()

    override fun resolve(type: KType, chain: ResolverChain): Any {
        return java.net.URL("https://example.com")
    }
}
```

- Use `typeOf<T>()` for exact type matching. Avoid `type.toString().contains(...)` — it can cause false positives.
- `resolve()` returns `Any?`. Delegate nested type resolution to `chain.resolve(type)` when needed.

### 2. Access strategies

Resolvers that need strategies receive a `StrategyProvider` via the provider. Retrieve a strategy with the reified `get()` extension and fall back to a sensible default:

```kotlin
import dev.appoutlet.some.config.StringStrategy
import dev.appoutlet.some.core.StrategyProvider
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.get
import kotlin.random.Random

class UrlResolver(
    strategyProvider: StrategyProvider,
    private val random: Random,
) : Resolver {
    private val stringStrategy = strategyProvider.get<StringStrategy>() ?: StringStrategy.default

    // ...
}
```

### 3. Create the provider

The provider is the entry point that Some discovers via `ServiceLoader`. It receives the `StrategyProvider` and `Random` from the current configuration so that contributed resolvers stay consistent with the rest of the chain:

```kotlin
import dev.appoutlet.some.core.StrategyProvider
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverProvider
import kotlin.random.Random

class UrlResolverProvider : ResolverProvider {
    override fun createResolvers(
        strategyProvider: StrategyProvider,
        random: Random,
    ): List<Resolver> = listOf(UrlResolver(strategyProvider, random))
}
```

### 4. Register the provider

Create a file at:

```
META-INF/services/dev.appoutlet.some.core.ResolverProvider
```

containing the fully qualified class name of your provider:

```
com.example.some.UrlResolverProvider
```

Place this file in your JAR's `resources/META-INF/services/` directory. Some will discover it automatically at runtime.

#### Using @AutoService

If you use the [autoservice-ir](https://github.com/joshfriend/autoservice-ir) compiler plugin, you can annotate your provider instead of maintaining the service file manually:

```kotlin
import com.fueledbycaffeine.autoservice.AutoService
import dev.appoutlet.some.core.ResolverProvider

@AutoService
class UrlResolverProvider : ResolverProvider {
    // ...
}
```

Then apply the plugin in `build.gradle.kts`:

```kotlin
plugins {
    id("com.fueledbycaffeine.autoservice") version "0.1.5"
}
```

## Full example

Here is a complete example that adds support for `java.net.URL`:

```kotlin
// UrlResolver.kt
package com.example.some

import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.StrategyProvider
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.config.StringStrategy
import dev.appoutlet.some.core.get
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class UrlResolver(
    private val strategyProvider: StrategyProvider,
    private val random: Random,
) : Resolver {
    private val stringStrategy = strategyProvider.get<StringStrategy>() ?: StringStrategy.default

    override fun canResolve(type: KType): Boolean = type == typeOf<java.net.URL>()

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val host = when (stringStrategy) {
            is StringStrategy.Readable -> "example-${random.nextInt(1000)}.com"
            else -> "example.com"
        }
        return java.net.URL("https://$host")
    }
}
```

```kotlin
// UrlResolverProvider.kt
package com.example.some

import dev.appoutlet.some.core.StrategyProvider
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverProvider
import kotlin.random.Random

class UrlResolverProvider : ResolverProvider {
    override fun createResolvers(
        strategyProvider: StrategyProvider,
        random: Random,
    ): List<Resolver> = listOf(UrlResolver(strategyProvider, random))
}
```

With this provider on the classpath, any project that depends on your library — or any module in your own application — can generate `URL` values without additional configuration:

```kotlin
val url: java.net.URL = some()
// https://example-42.com
```

## Delegating to the resolver chain

Resolvers can delegate nested type resolution to `chain.resolve(type)`. This is useful when your custom type contains fields that Some should still generate automatically:

```kotlin
data class Email(val address: String)

class EmailResolver : Resolver {
    override fun canResolve(type: KType): Boolean = type == typeOf<Email>()

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val address = chain.resolve(typeOf<String>()) as String
        return Email("$address@example.com")
    }
}
```

## Custom strategies

Your resolver can define its own strategy to let consumers control its behavior. A strategy is any class that implements the `Strategy` marker interface and provides a `key` property:

```kotlin
import dev.appoutlet.some.config.Strategy
import kotlin.reflect.KClass

sealed interface UrlStrategy : Strategy {
    override val key get() = UrlStrategy::class

    data class Fixed(val url: String = "https://example.com") : UrlStrategy
    data class RandomHost(val tld: String = "com") : UrlStrategy

    companion object {
        val default: UrlStrategy get() = Fixed()
    }
}
```

Register the strategy like any built-in one:

```kotlin
val url = some<java.net.URL> {
    strategy(UrlStrategy.RandomHost(tld = "org"))
}
```

Then retrieve it in your resolver through the `StrategyProvider`:

```kotlin
class UrlResolver(
    strategyProvider: StrategyProvider,
    private val random: Random,
) : Resolver {
    private val urlStrategy = strategyProvider.get<UrlStrategy>() ?: UrlStrategy.default

    override fun canResolve(type: KType): Boolean = type == typeOf<java.net.URL>()

    override fun resolve(type: KType, chain: ResolverChain): Any = when (urlStrategy) {
        is UrlStrategy.Fixed -> java.net.URL(urlStrategy.url)
        is UrlStrategy.RandomHost -> {
            val host = "host-${random.nextInt(1000)}.${urlStrategy.tld}"
            java.net.URL("https://$host")
        }
    }
}
```

When no strategy is registered, the resolver falls back to `UrlStrategy.default` — the same pattern used by all built-in resolvers.

## Custom factories vs. custom resolvers

| | Custom factory | Custom resolver |
|---|---|---|
| **Registered by** | Test code via `factory()` or `property()` | `ServiceLoader` (library or app module) |
| **Priority** | Highest — always wins | Between nullable and built-in |
| **Access to chain** | No (direct value construction) | Yes (can delegate via `chain.resolve`) |
| **Access to strategies** | Via `FixtureContext.strategyProvider` | Via `StrategyProvider` parameter |
| **Best for** | One-off overrides in test suites | Reusable extensions, internal app types, libraries |

## Error handling

If a `ResolverProvider` throws during discovery or resolver creation, Some catches the error and continues with the remaining providers. The built-in resolver chain is always available as a fallback. This means your application will still work even if an extension fails to load.

[Resolver]: ../reference/latest/index.html