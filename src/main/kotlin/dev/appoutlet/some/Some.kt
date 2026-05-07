package dev.appoutlet.some

import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.TypeResolver
import kotlin.random.Random
import kotlin.reflect.typeOf

class Some(
    val resolvers: List<TypeResolver>,
    val random: Random,
    val config: SomeConfig
) {
    @Suppress("MemberNameEqualsClassName")
    inline fun <reified T> some(): T {
        val session = ResolverChain(resolvers, config.nullableStrategy)
        return session.resolve(typeOf<T>()) as T
    }

    inline operator fun <reified T> invoke(): T = some()

    inline operator fun <reified T> invoke(noinline config: SomeConfig.() -> Unit = {}): T {
        val aggregatedConfig = this.config.copy().apply(config)
        return Some(aggregatedConfig.buildResolvers(random), random, aggregatedConfig).some()
    }
}

fun someSetup(config: SomeConfig.() -> Unit = {}): Some {
    val someConfig = SomeConfig().apply(config)
    val random = someConfig.buildRandom()
    return Some(someConfig.buildResolvers(random), random, someConfig)
}

val defaultConfig: SomeConfig by lazy { SomeConfig() }
val defaultResolvers: List<TypeResolver> by lazy { defaultConfig.buildResolvers() }

inline fun <reified T> some(): T {
    val session = ResolverChain(defaultResolvers, defaultConfig.nullableStrategy)
    return session.resolve(typeOf<T>()) as T
}

inline fun <reified T> some(noinline config: SomeConfig.() -> Unit = {}): T {
    val someSetup = someSetup(config)
    return someSetup.some<T>()
}
