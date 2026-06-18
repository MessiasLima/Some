package dev.appoutlet.some.config

import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.StrategyProvider
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.TypeResolverProvider
import dev.appoutlet.some.logging.logger
import dev.appoutlet.some.resolver.ArrayResolver
import dev.appoutlet.some.resolver.BigDecimalResolver
import dev.appoutlet.some.resolver.BigIntegerResolver
import dev.appoutlet.some.resolver.BooleanResolver
import dev.appoutlet.some.resolver.ByteResolver
import dev.appoutlet.some.resolver.CharResolver
import dev.appoutlet.some.resolver.ClassResolver
import dev.appoutlet.some.resolver.CustomTypeFactoryResolver
import dev.appoutlet.some.resolver.DoubleResolver
import dev.appoutlet.some.resolver.EnumResolver
import dev.appoutlet.some.resolver.FloatResolver
import dev.appoutlet.some.resolver.IntResolver
import dev.appoutlet.some.resolver.JavaDurationResolver
import dev.appoutlet.some.resolver.JavaInstantResolver
import dev.appoutlet.some.resolver.JavaUuidResolver
import dev.appoutlet.some.resolver.JavaZonedDateTimeResolver
import dev.appoutlet.some.resolver.KotlinDurationResolver
import dev.appoutlet.some.resolver.KotlinInstantResolver
import dev.appoutlet.some.resolver.KotlinUuidResolver
import dev.appoutlet.some.resolver.ListResolver
import dev.appoutlet.some.resolver.LocalDateResolver
import dev.appoutlet.some.resolver.LocalDateTimeResolver
import dev.appoutlet.some.resolver.LongResolver
import dev.appoutlet.some.resolver.MapResolver
import dev.appoutlet.some.resolver.NullableResolver
import dev.appoutlet.some.resolver.NumberResolver
import dev.appoutlet.some.resolver.ObjectResolver
import dev.appoutlet.some.resolver.OptionalResolver
import dev.appoutlet.some.resolver.SealedClassResolver
import dev.appoutlet.some.resolver.SetResolver
import dev.appoutlet.some.resolver.ShortResolver
import dev.appoutlet.some.resolver.StringResolver
import dev.appoutlet.some.resolver.ValueClassResolver
import java.util.ServiceLoader
import kotlin.random.Random
import kotlin.reflect.KClass

/**
 * Immutable configuration for fixture generation behavior.
 *
 * Use [SomeConfigBuilder] through `someSetup { ... }` or `some<T> { ... }` to configure.
 * Direct construction is useful for tests that need to explicitly assemble configurations.
 * Use [toBuilder] to derive a mutable copy of this configuration.
 *
 * @param strategies Map of registered strategy instances.
 * @param seed Seed for reproducible random generation. When `null`, [Random.Default] is used.
 * @param typeFactories Custom type factories keyed by class.
 * @param propertyFactories Custom property factories keyed by class and property name.
 */
data class SomeConfig(
    val strategies: Map<KClass<out Strategy>, Strategy> = defaultStrategies(),
    val seed: Long? = null,
    val typeFactories: Map<KClass<*>, FixtureContext.() -> Any?> = emptyMap(),
    val propertyFactories: Map<Pair<KClass<*>, String>, FixtureContext.() -> Any?> = emptyMap(),
) : StrategyProvider {
    private val strategyProvider: StrategyProvider = DefaultStrategyProvider(strategies)
    private val logger by logger()

    /**
     * Returns the strategy registered for [key].
     *
     * @param key The [KClass] of the strategy to retrieve (e.g. [NullableStrategy::class]).
     * @return The registered strategy instance.
     * @throws NoSuchElementException when no strategy is registered for [key].
     */
    override fun <T : Strategy> get(key: KClass<T>): T? = strategyProvider[key]

    /**
     * Creates a [SomeConfigBuilder] pre-populated with this configuration's values.
     *
     * The returned builder receives copies of the strategy, factory, and property maps,
     * so changing it does not mutate this immutable configuration.
     *
     * @return A [SomeConfigBuilder] containing this configuration's current state.
     */
    fun toBuilder(): SomeConfigBuilder = SomeConfigBuilder().apply {
        populateStrategies(this@SomeConfig.strategies)
        seed = this@SomeConfig.seed
        populateTypeFactories(this@SomeConfig.typeFactories)
        populatePropertyFactories(this@SomeConfig.propertyFactories)
    }

    /**
     * Creates the resolver chain used to generate fixture values.
     *
     * Resolver order defines precedence: the first resolver that supports a type is used.
     *
     * 1. [CustomTypeFactoryResolver] is first so explicit user factories override everything.
     * 2. [NullableResolver] handles nullable wrappers before any concrete type resolver.
     * 3. Third-party resolvers discovered via [java.util.ServiceLoader] come next, allowing external libraries to
     *    override built-in behavior for basic types.
     * 4. Built-in resolvers handle remaining standard types.
     * 5. [ClassResolver] is last because it is the fallback for constructable model classes and is also where
     *    property factories are applied.
     *
     * Resolvers that need strategies receive a [StrategyProvider]; the rest receive only the shared [Random] source.
     * This keeps resolver construction free of strategy-specific knowledge.
     *
     * @param random Random source shared by resolvers that generate randomized values.
     * @return The ordered [TypeResolver] list for this configuration.
     */
    fun buildResolvers(random: Random = buildRandom()): List<TypeResolver> {
        val builtInResolvers = listOf(
            ObjectResolver(),
            EnumResolver(random),
            SealedClassResolver(random),
            ValueClassResolver(),
            StringResolver(strategyProvider, random),
            IntResolver(random),
            LongResolver(random),
            DoubleResolver(random),
            FloatResolver(strategyProvider, random),
            BooleanResolver(random),
            CharResolver(random),
            ByteResolver(random),
            ShortResolver(random),
            NumberResolver(random),
            KotlinUuidResolver(),
            KotlinInstantResolver(random),
            KotlinDurationResolver(random),
            JavaUuidResolver(),
            JavaInstantResolver(random),
            JavaDurationResolver(random),
            JavaZonedDateTimeResolver(random),
            OptionalResolver(strategyProvider, random),
            BigDecimalResolver(random),
            BigIntegerResolver(random),
            LocalDateResolver(random),
            LocalDateTimeResolver(random),
            ListResolver(strategyProvider, random),
            SetResolver(strategyProvider, random),
            MapResolver(strategyProvider, random),
            ArrayResolver(strategyProvider, random),
        )

        val discoveredResolvers = discoverResolvers(strategyProvider, random)

        return listOf(
            CustomTypeFactoryResolver(strategyProvider, typeFactories, random),
            NullableResolver(strategyProvider, random),
        ) + discoveredResolvers + builtInResolvers + ClassResolver(strategyProvider, propertyFactories, random)
    }

    /**
     * Discovers additional [TypeResolver]s from third-party libraries via [java.util.ServiceLoader].
     *
     * Failures during discovery or provider instantiation are swallowed so the library always falls back to the
     * built-in resolver chain.
     *
     * @param strategyProvider Provider of all configured generation strategies.
     * @param random Random source shared by discovered resolvers.
     * @return Ordered list of discovered resolvers, or an empty list if discovery fails.
     */
    @Suppress("TooGenericExceptionCaught")
    private fun discoverResolvers(
        strategyProvider: StrategyProvider,
        random: Random,
    ): List<TypeResolver> {
        val loader = ServiceLoader.load(TypeResolverProvider::class.java)
        val providers = mutableListOf<TypeResolverProvider>()
        val iterator = loader.iterator()

        while (iterator.hasNext()) {
            try {
                providers.add(iterator.next())
            } catch (e: Throwable) {
                logger.w(e) { "Failed to load a TypeResolverProvider implementation" }
            }
        }

        return providers.flatMap { provider ->
            try {
                provider.createResolvers(strategyProvider, random)
            } catch (e: Throwable) {
                logger.w(e) { "Failed to instantiate resolver provider: ${provider::class.simpleName}" }
                emptyList()
            }
        }
    }

    /**
     * Creates a [Random] instance for this configuration.
     *
     * A configured [seed] makes fixture generation reproducible for all resolvers sharing the returned random source.
     * Without a seed, the non-deterministic [Random.Default] source is used.
     *
     * @return [Random] seeded with [seed] if set, or [Random.Default] otherwise.
     */
    internal fun buildRandom(): Random = seed?.let { Random(it) } ?: Random.Default

    companion object {
        /**
         * Returns the default strategies for fixture generation.
         */
        fun defaultStrategies(): Map<KClass<out Strategy>, Strategy> = mapOf(
            NullableStrategy::class to NullableStrategy.default,
            StringStrategy::class to StringStrategy.default,
            CollectionStrategy::class to CollectionStrategy.default,
            FloatStrategy::class to FloatStrategy.default,
            DefaultValueStrategy::class to DefaultValueStrategy.default,
        )
    }
}
