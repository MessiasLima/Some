package dev.appoutlet.some

import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.config.SomeConfigBuilder
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.TypeResolver
import kotlin.random.Random
import kotlin.reflect.typeOf

/**
 * Fixture generator configured with a resolver chain and shared random source.
 *
 * Instances are created by [someSetup] and can be reused to generate multiple values with the same configuration.
 *
 * @param resolvers Ordered resolver list used to generate values.
 * @param random Random source shared by resolvers created for this instance.
 * @param config Configuration used by this generator.
 */
class Some(
    val resolvers: List<TypeResolver>,
    val random: Random,
    val config: SomeConfig
) {
    /**
     * Generates a fixture value of type [T] using this instance's configuration.
     *
     * @param T Type to generate.
     * @return Generated value of type [T].
     */
    @Suppress("MemberNameEqualsClassName")
    inline fun <reified T> some(): T {
        val session = ResolverChain(resolvers, config.nullableStrategy)
        return session.resolve(typeOf<T>()) as T
    }

    /**
     * Generates a fixture value of type [T].
     *
     * Enables concise usage such as `some<User>()` when `some` is a [Some] instance.
     *
     * @param T Type to generate.
     * @return Generated value of type [T].
     */
    inline operator fun <reified T> invoke(): T = some()

    /**
     * Generates a fixture value of type [T] with per-call configuration overrides.
     *
     * Overrides are applied to a copy of this instance's configuration and do not mutate the original [Some].
     *
     * @param T Type to generate.
     * @param config Configuration overrides for this call.
     * @return Generated value of type [T].
     */
    inline operator fun <reified T> invoke(noinline config: SomeConfigBuilder.() -> Unit = {}): T {
        val aggregatedConfig = this.config.toBuilder().apply(config).build()
        return Some(aggregatedConfig.buildResolvers(random), random, aggregatedConfig).some()
    }
}

/**
 * Creates a reusable [Some] generator.
 *
 * Use this when multiple fixtures should share the same configuration and random source.
 *
 * @param config Configuration applied to the created generator.
 * @return A configured [Some] instance.
 */
fun someSetup(config: SomeConfigBuilder.() -> Unit = {}): Some {
    val someConfig = SomeConfigBuilder().apply(config).build()
    val random = someConfig.buildRandom()
    return Some(someConfig.buildResolvers(random), random, someConfig)
}

/**
 * Lazily-created default configuration used by top-level [some].
 */
val defaultConfig: SomeConfig by lazy { SomeConfig() }

/**
 * Lazily-created default resolver chain used by top-level [some].
 */
val defaultResolvers: List<TypeResolver> by lazy { defaultConfig.buildResolvers() }

/**
 * Generates a fixture value using the default configuration.
 *
 * @param T Type to generate.
 * @return Generated value of type [T].
 */
inline fun <reified T> some(): T {
    val session = ResolverChain(defaultResolvers, defaultConfig.nullableStrategy)
    return session.resolve(typeOf<T>()) as T
}

/**
 * Generates a fixture value using one-off configuration overrides.
 *
 * @param T Type to generate.
 * @param config Configuration applied only to this generation call.
 * @return Generated value of type [T].
 */
inline fun <reified T> some(noinline config: SomeConfigBuilder.() -> Unit = {}): T {
    val someSetup = someSetup(config)
    return someSetup.some<T>()
}
