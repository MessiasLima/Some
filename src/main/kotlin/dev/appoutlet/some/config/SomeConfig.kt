package dev.appoutlet.some.config

import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.resolver.ArrayResolver
import dev.appoutlet.some.resolver.BigDecimalResolver
import dev.appoutlet.some.resolver.BigIntegerResolver
import dev.appoutlet.some.resolver.BooleanResolver
import dev.appoutlet.some.resolver.ByteResolver
import dev.appoutlet.some.resolver.CharResolver
import dev.appoutlet.some.resolver.CustomTypeFactoryResolver
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
 * Controls nullable handling, string generation, collection sizing, random seeding, type factories, and property
 * factories. Each generation call uses a [SomeConfig] to build an ordered resolver list, and that resolver order
 * defines which customization wins when multiple options could apply.
 *
 * Prefer [SomeConfigBuilder] through `some { ... }` or `someSetup { ... }` for user-facing configuration. Direct
 * construction is useful for tests or library integrations that need to assemble configuration values explicitly.
 * Use [toBuilder] to derive a mutable copy without mutating the original configuration.
 *
 * @param nullableStrategy Strategy for handling nullable type resolution.
 * @param stringStrategy Strategy for generating values handled by [StringResolver].
 * @param collectionStrategy Strategy for collection sizes handled by collection resolvers.
 * @param seed Seed for reproducible random generation. When `null`, [Random.Default] is used.
 * @param typeFactories Custom type factories keyed by the exact class they override. These are resolved before all
 * built-in resolvers.
 * @param propertyFactories Custom property factories keyed by owning class and constructor parameter name. These are
 * applied by [DataClassResolver] while constructing model objects.
 */
data class SomeConfig(
    val nullableStrategy: NullableStrategy = NullableStrategy.NullOnCircularReference,
    val stringStrategy: StringStrategy = StringStrategy.Random(),
    val collectionStrategy: CollectionStrategy = CollectionStrategy(),
    val seed: Long? = null,
    val typeFactories: Map<KClass<*>, FixtureContext.() -> Any?> = emptyMap(),
    val propertyFactories: Map<Pair<KClass<*>, String>, FixtureContext.() -> Any?> = emptyMap(),
) {
    /**
     * Creates a [SomeConfigBuilder] pre-populated with this configuration's values.
     *
     * This is used by reusable [dev.appoutlet.some.Some] instances to apply per-call overrides. The returned builder
     * receives copies of the factory maps, so changing it does not mutate this immutable configuration.
     *
     * @return A [SomeConfigBuilder] containing this configuration's current state.
     */
    fun toBuilder(): SomeConfigBuilder = SomeConfigBuilder().apply {
        nullableStrategy = this@SomeConfig.nullableStrategy
        stringStrategy = this@SomeConfig.stringStrategy
        collectionStrategy = this@SomeConfig.collectionStrategy
        seed = this@SomeConfig.seed
        populateTypeFactories(this@SomeConfig.typeFactories)
        populatePropertyFactories(this@SomeConfig.propertyFactories)
    }

    /**
     * Creates the resolver chain used to generate fixture values.
     *
     * Resolver order defines precedence: the first resolver that supports a type is used. Type factories are first so
     * explicit type-level user configuration overrides built-in behavior. [DataClassResolver] is last because it is the
     * fallback for constructable model classes and is also where property factories are applied.
     *
     * @param random Random source shared by resolvers that generate randomized values.
     * @return The ordered [TypeResolver] list for this configuration.
     */
    fun buildResolvers(random: Random = buildRandom()): List<TypeResolver> {
        return listOf(
            CustomTypeFactoryResolver(typeFactories, random, nullableStrategy, stringStrategy, collectionStrategy),
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
            DataClassResolver(
                propertyFactories = propertyFactories,
                random = random,
                nullableStrategy = nullableStrategy,
                stringStrategy = stringStrategy,
                collectionStrategy = collectionStrategy
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
