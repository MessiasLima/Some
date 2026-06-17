package dev.appoutlet.some

import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.config.SomeConfigBuilder
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.get
import dev.appoutlet.some.resolver.nullable.NullableStrategy
import kotlin.random.Random
import kotlin.reflect.typeOf

class Some(
    val resolvers: List<TypeResolver>,
    val random: Random,
    val config: SomeConfig
) {
    inline fun <reified T : Any> some(): T {
        val strategy = config.get<NullableStrategy>() ?: NullableStrategy.default
        val chain = ResolverChain(resolvers, strategy)
        return chain.resolve(typeOf<T>()) as T
    }
}

inline fun <reified T : Any> some(noinline configuration: (SomeConfigBuilder.() -> Unit)? = null): T {
    val config = if (configuration != null) {
        buildSomeConfig(configuration)
    } else {
        defaultSomeConfig
    }
    val some = Some(config.buildResolvers(), config.buildRandom(), config)
    return some.some<T>()
}

fun someSetup(configuration: SomeConfigBuilder.() -> Unit = {}): Some {
    val config = buildSomeConfig(configuration)
    return Some(config.buildResolvers(), config.buildRandom(), config)
}

@PublishedApi
internal val defaultSomeConfig: SomeConfig by lazy { buildSomeConfig() }

@PublishedApi
internal val defaultResolvers: List<TypeResolver> by lazy {
    defaultSomeConfig.buildResolvers()
}

@PublishedApi
internal inline fun <reified T : Any> someDefault(): T {
    val strategy = defaultSomeConfig.get<NullableStrategy>() ?: NullableStrategy.default
    val chain = ResolverChain(defaultResolvers, strategy)
    return chain.resolve(typeOf<T>()) as T
}

fun buildSomeConfig(configuration: SomeConfigBuilder.() -> Unit = {}): SomeConfig {
    return SomeConfigBuilder().apply(configuration).build()
}
