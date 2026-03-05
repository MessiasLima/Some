package dev.appoutlet.some

import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.typeOf

class Some(
    val chain: ResolverChain,
    val random: Random,
    val config: SomeConfig
) {
    inline fun <reified T> some(): T {
        return chain.resolve(typeOf<T>(), FixtureContext(random, emptyList(), config)) as T
    }

    inline operator fun <reified T> invoke(): T = some()

    inline operator fun <reified T> invoke(noinline config: SomeConfig.() -> Unit = {}): T {
        val aggregatedConfig = this.config.copy().apply(config)
        return Some(aggregatedConfig.buildChain(), random, aggregatedConfig).some()
    }
}

fun someSetup(config: SomeConfig.() -> Unit = {}): Some {
    val someConfig = SomeConfig().apply(config)
    return Some(someConfig.buildChain(), someConfig.buildRandom(), someConfig)
}

val defaultChain: ResolverChain by lazy { SomeConfig().buildChain() }
val defaultRandom: Random by lazy { Random.Default }

inline fun <reified T> some(): T {
    return defaultChain.resolve(typeOf<T>(), FixtureContext(defaultRandom, emptyList(), SomeConfig())) as T
}

inline fun <reified T> some(noinline config: SomeConfig.() -> Unit = {}): T {
    val someSetup = someSetup(config)
    return someSetup.some<T>()
}