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
 * Configuration for customizing the behavior of [some] fixture generation.
 *
 * Controls strategies for nullable types, strings, collections, random seeding,
 * and custom factory registrations.
 *
 * @param nullableStrategy Strategy for handling nullable type resolution. Defaults to [NullableStrategy.NullOnCircularReference].
 * @param stringStrategy Strategy for generating string values. Defaults to [StringStrategy.Random].
 * @param collectionStrategy Strategy for generating collection sizes. Defaults to [CollectionStrategy].
 * @param seed Seed for reproducible random generation. If null, uses [Random.Default].
 * @param factories Map of custom factory functions registered for specific types.
 */
data class SomeConfig(
    var nullableStrategy: NullableStrategy = NullableStrategy.NullOnCircularReference,
    var stringStrategy: StringStrategy = StringStrategy.Random(),
    var collectionStrategy: CollectionStrategy = CollectionStrategy(),
    var seed: Long? = null,
    val factories: MutableMap<KClass<*>, FixtureContext.() -> Any?> = mutableMapOf(),
) {
    fun <T : Any> register(kClass: KClass<T>, factory: FixtureContext.() -> T) {
        factories[kClass] = factory
    }

    /**
     * Returns a copy of this configuration with an independent factories map.
     *
     * The strategy values are reused unless replacement values are provided.
     *
     * @param nullableStrategy Nullable strategy for the copied configuration.
     * @param stringStrategy String strategy for the copied configuration.
     * @param collectionStrategy Collection strategy for the copied configuration.
     * @param seed Random seed for the copied configuration.
     * @return A new [SomeConfig] containing the provided values and copied factory registrations.
     */
    fun copy(
        nullableStrategy: NullableStrategy = this.nullableStrategy,
        stringStrategy: StringStrategy = this.stringStrategy,
        collectionStrategy: CollectionStrategy = this.collectionStrategy,
        seed: Long? = this.seed,
    ): SomeConfig {
        return SomeConfig(
            nullableStrategy = nullableStrategy,
            stringStrategy = stringStrategy,
            collectionStrategy = collectionStrategy,
            seed = seed,
            factories = this.factories.toMutableMap()
        )
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

    internal fun buildRandom(): Random = seed?.let { Random(it) } ?: Random.Default
}
