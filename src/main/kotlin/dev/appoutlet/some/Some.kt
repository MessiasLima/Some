package dev.appoutlet.some

import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.typeOf

public class Some(
    public val chain: ResolverChain,
    public val random: Random,
    public val config: SomeConfig
) {
    public inline fun <reified T> some(): T {
        return chain.resolve(typeOf<T>(), FixtureContext(random, emptyList(), config)) as T
    }
}

public fun some(config: SomeConfig.() -> Unit = {}): Some {
    val someConfig = SomeConfig().apply(config)
    return Some(someConfig.buildChain(), someConfig.buildRandom(), someConfig)
}

public val defaultChain: ResolverChain by lazy { SomeConfig().buildChain() }
public val defaultRandom: Random by lazy { Random.Default }

public inline fun <reified T> some(): T {
    return defaultChain.resolve(typeOf<T>(), FixtureContext(defaultRandom, emptyList(), SomeConfig())) as T
}
