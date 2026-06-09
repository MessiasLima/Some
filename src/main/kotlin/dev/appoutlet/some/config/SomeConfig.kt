package dev.appoutlet.some.config

import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.StrategyProvider
import dev.appoutlet.some.core.TypeResolver
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
import dev.appoutlet.some.resolver.KotlinDurationResolver
import dev.appoutlet.some.resolver.KotlinInstantResolver
import dev.appoutlet.some.resolver.KotlinUuidResolver
import dev.appoutlet.some.resolver.ListResolver
import dev.appoutlet.some.resolver.LocalDateResolver
import dev.appoutlet.some.resolver.LocalDateTimeResolver
import dev.appoutlet.some.resolver.LongResolver
import dev.appoutlet.some.resolver.MapResolver
import dev.appoutlet.some.resolver.NullableResolver
import dev.appoutlet.some.resolver.ObjectResolver
import dev.appoutlet.some.resolver.SealedClassResolver
import dev.appoutlet.some.resolver.SetResolver
import dev.appoutlet.some.resolver.ShortResolver
import dev.appoutlet.some.resolver.StringResolver
import dev.appoutlet.some.resolver.ValueClassResolver
import kotlin.random.Random
import kotlin.reflect.KClass

/**
 * Immutable configuration for customizing the behavior of [some][dev.appoutlet.some.some] fixture generation.
 *
 * Controls strategy registration, random seeding, type factories, and property factories. Strategies are
 * stored in a map keyed by their base [KClass] and are retrieved through the [StrategyProvider] interface.
 * Each generation call uses a [SomeConfig] to build an ordered resolver list, and that resolver order defines
 * which customization wins when multiple options could apply.
 *
 * Prefer [SomeConfigBuilder] through `some { ... }` or `someSetup { ... }` for user-facing configuration. Direct
 * construction is useful for tests or library integrations that need to assemble configuration values explicitly.
 * Use [toBuilder] to derive a mutable copy without mutating the original configuration.
 *
 * ## Strategy registration
 *
 * Built-in strategies are pre-registered with sensible defaults. Override them or add custom strategies
 * using the [strategy] method:
 *
 * ```kotlin
 * val config = SomeConfig().strategy(NullableStrategy.AlwaysNull)
 * ```
 *
 * Custom strategies can be retrieved from any [StrategyProvider] (including [FixtureContext]) by key:
 *
 * ```kotlin
 * val myStrategy = config[MyCustomStrategy::class]
 * ```
 *
 * @param strategies Strategy instances keyed by their base [KClass].
 * @param seed Seed for reproducible random generation. When `null`, [Random.Default] is used.
 * @param typeFactories Custom type factories keyed by the exact class they override. These are resolved before all
 * built-in resolvers.
 * @param propertyFactories Custom property factories keyed by owning class and constructor parameter name. These are
 * applied by [ClassResolver] while constructing model objects.
 */
data class SomeConfig(
    val strategies: Map<KClass<out Strategy>, Strategy> = defaultStrategies(),
    val seed: Long? = null,
    val typeFactories: Map<KClass<*>, FixtureContext.() -> Any?> = emptyMap(),
    val propertyFactories: Map<Pair<KClass<*>, String>, FixtureContext.() -> Any?> = emptyMap(),
) : StrategyProvider {

    companion object {
        /**
         * Returns the default strategy map with sensible defaults for all built-in strategies.
         */
        fun defaultStrategies(): Map<KClass<out Strategy>, Strategy> = mapOf(
            NullableStrategy::class to NullableStrategy.NullOnCircularReference,
            StringStrategy::class to StringStrategy.Random(),
            CollectionStrategy::class to CollectionStrategy(),
            DefaultValueStrategy::class to DefaultValueStrategy.UseDefault,
        )
    }

    /**
     * Returns the strategy registered for [key].
     *
     * @param key The [KClass] of the strategy to retrieve (e.g. [NullableStrategy::class]).
     * @return The registered strategy instance.
     * @throws NoSuchElementException when no strategy is registered for [key].
     */
    override fun <T : Strategy> get(key: KClass<T>): T {
        @Suppress("UNCHECKED_CAST")
        return strategies[key] as? T
            ?: throw NoSuchElementException("No strategy registered for ${key.simpleName}")
    }

    /**
     * Returns a new [SomeConfig] with [strategy] registered under its base type key.
     *
     * The key is determined by the strategy's immediate [Strategy] subtype. For example,
     * registering [NullableStrategy.AlwaysNull] replaces the [NullableStrategy] entry.
     *
     * @param strategy The strategy instance to register.
     * @return A new [SomeConfig] with the strategy registered.
     */
    fun <T : Strategy> strategy(strategy: T): SomeConfig {
        return copy(strategies = strategies + (findStrategyKey(strategy) to strategy))
    }

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
     * Resolver order defines precedence: the first resolver that supports a type is used. Type factories are first so
     * explicit user configuration overrides built-in behavior. [ClassResolver] is last because it is the
     * fallback for constructable model classes and is also where property factories are applied.
     *
     * @param random Random source shared by resolvers that generate randomized values.
     * @return The ordered [TypeResolver] list for this configuration.
     */
    fun buildResolvers(random: Random = buildRandom()): List<TypeResolver> {
        return listOf(
            CustomTypeFactoryResolver(
                typeFactories = typeFactories,
                random = random,
                strategyProvider = this,
            ),
            NullableResolver(this, random),
            ObjectResolver(),
            EnumResolver(random),
            SealedClassResolver(random),
            ValueClassResolver(),
            StringResolver(this, random),
            IntResolver(random),
            LongResolver(random),
            DoubleResolver(random),
            FloatResolver(random),
            BooleanResolver(random),
            CharResolver(random),
            ByteResolver(random),
            ShortResolver(random),
            KotlinUuidResolver(),
            KotlinInstantResolver(random),
            KotlinDurationResolver(random),
            JavaUuidResolver(),
            JavaInstantResolver(random),
            JavaDurationResolver(random),
            BigDecimalResolver(random),
            BigIntegerResolver(random),
            LocalDateResolver(random),
            LocalDateTimeResolver(random),
            ListResolver(this, random),
            SetResolver(this, random),
            MapResolver(this, random),
            ArrayResolver(this, random),
            ClassResolver(
                propertyFactories = propertyFactories,
                random = random,
                strategyProvider = this,
            )
        )
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
}

/**
 * Determines the strategy map key for a given [Strategy] instance.
 *
 * For sealed strategy hierarchies ([NullableStrategy], [StringStrategy], [DefaultValueStrategy]),
 * the key is the sealed interface [KClass] so that any implementation replaces the same entry.
 * For data-class strategies ([CollectionStrategy]), the class itself is the key.
 * Custom strategies that directly implement [Strategy] use their own class as the key.
 */
internal fun findStrategyKey(strategy: Strategy): KClass<out Strategy> {
    return when (strategy) {
        is NullableStrategy -> NullableStrategy::class
        is StringStrategy -> StringStrategy::class
        is CollectionStrategy -> CollectionStrategy::class
        is DefaultValueStrategy -> DefaultValueStrategy::class
        else -> strategy::class
    }
}
