package dev.appoutlet.some.config

import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.resolver.ArrayResolver
import dev.appoutlet.some.resolver.BigDecimalResolver
import dev.appoutlet.some.resolver.BigIntegerResolver
import dev.appoutlet.some.resolver.BooleanResolver
import dev.appoutlet.some.resolver.ByteResolver
import dev.appoutlet.some.resolver.CharResolver
import dev.appoutlet.some.resolver.CustomFactoryResolver
import dev.appoutlet.some.resolver.DataClassResolver
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
 * Controls strategies for nullable types, strings, collections, random seeding,
 * and custom factory registrations.
 *
 * Use [SomeConfigBuilder] to create instances via the DSL-style configuration lambdas,
 * or construct directly. Use [toBuilder] to derive a new configuration from an existing one.
 *
 * @param nullableStrategy Strategy for handling nullable type resolution.
 *  Defaults to [NullableStrategy.NullOnCircularReference].
 * @param stringStrategy Strategy for generating string values. Defaults to [StringStrategy.Random].
 * @param collectionStrategy Strategy for generating collection sizes. Defaults to [CollectionStrategy].
 * @param seed Seed for reproducible random generation. If null, uses [Random.Default].
 * @param factories Map of custom factory functions registered for specific types.
 */
data class SomeConfig(
    val nullableStrategy: NullableStrategy = NullableStrategy.NullOnCircularReference,
    val stringStrategy: StringStrategy = StringStrategy.Random(),
    val collectionStrategy: CollectionStrategy = CollectionStrategy(),
    val seed: Long? = null,
    val factories: Map<KClass<*>, FixtureContext.() -> Any?> = emptyMap(),
) {
    /**
     * Creates a [SomeConfigBuilder] pre-populated with this configuration's values.
     *
     * Useful for deriving new configurations from an existing one without mutation.
     *
     * @return A [SomeConfigBuilder] containing this configuration's current state.
     */
    fun toBuilder(): SomeConfigBuilder = SomeConfigBuilder().apply {
        nullableStrategy = this@SomeConfig.nullableStrategy
        stringStrategy = this@SomeConfig.stringStrategy
        collectionStrategy = this@SomeConfig.collectionStrategy
        seed = this@SomeConfig.seed
        populateFactories(this@SomeConfig.factories)
    }

    /**
     * Creates the resolver chain used to generate fixture values.
     *
     * Resolver order defines precedence: the first resolver that supports a type is used.
     *
     * @param random Random source shared by resolvers that generate randomized values.
     * @return The ordered [TypeResolver] list for this configuration.
     */
    fun buildResolvers(random: Random = buildRandom()): List<TypeResolver> {
        return listOf(
            CustomFactoryResolver(factories, random, nullableStrategy, stringStrategy, collectionStrategy),
            NullableResolver(nullableStrategy, random),
            ObjectResolver(),
            EnumResolver(random),
            SealedClassResolver(random),
            ValueClassResolver(),
            StringResolver(stringStrategy, random),
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
            ListResolver(collectionStrategy, random),
            SetResolver(collectionStrategy, random),
            MapResolver(collectionStrategy, random),
            ArrayResolver(collectionStrategy, random),
            DataClassResolver()
        )
    }

    /**
     * Creates a [Random] instance for this configuration.
     *
     * @return [Random] seeded with [seed] if set, or [Random.Default] otherwise.
     */
    internal fun buildRandom(): Random = seed?.let { Random(it) } ?: Random.Default
}
