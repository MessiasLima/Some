package dev.appoutlet.some.config

import dev.appoutlet.some.core.FixtureContext
import kotlin.reflect.KClass

/**
 * Mutable builder for creating immutable [SomeConfig] instances.
 *
 * Used as the receiver in DSL-style configuration lambdas such as [dev.appoutlet.some.someSetup]
 * and [dev.appoutlet.some.some] overrides. After configuration, call [build] to produce
 * an immutable [SomeConfig].
 */
class SomeConfigBuilder {
    /**
     * Strategy for handling nullable type resolution.
     * Defaults to [NullableStrategy.NullOnCircularReference].
     */
    var nullableStrategy: NullableStrategy = NullableStrategy.NullOnCircularReference

    /**
     * Strategy for generating string values.
     * Defaults to [StringStrategy.Random].
     */
    var stringStrategy: StringStrategy = StringStrategy.Random()

    /**
     * Strategy for generating collection sizes.
     * Defaults to [CollectionStrategy] with a range of 1..5.
     */
    var collectionStrategy: CollectionStrategy = CollectionStrategy()

    /**
     * Seed for reproducible random generation.
     * If null, uses [kotlin.random.Random.Default].
     */
    var seed: Long? = null

    private val _factories: MutableMap<KClass<*>, FixtureContext.() -> Any?> = mutableMapOf()

    /**
     * Registers a custom factory function for type [T].
     *
     * Custom factories take priority over built-in resolvers.
     *
     * @param T The type this factory produces.
     * @param kClass The [KClass] of the type to override.
     * @param factory Lambda receiving a [FixtureContext] and returning a value of type [T].
     */
    fun <T : Any> register(kClass: KClass<T>, factory: FixtureContext.() -> T) {
        _factories[kClass] = factory
    }

    /**
     * Populates the builder's factory map with entries from an existing map.
     *
     * Used internally by [SomeConfig.toBuilder] to transfer factory registrations.
     *
     * @param factories Map of factory registrations to copy into this builder.
     */
    internal fun populateFactories(factories: Map<KClass<*>, FixtureContext.() -> Any?>) {
        _factories.putAll(factories)
    }

    /**
     * Builds an immutable [SomeConfig] from the current state of this builder.
     *
     * @return A new [SomeConfig] instance with the configured values.
     */
    fun build(): SomeConfig = SomeConfig(
        nullableStrategy = nullableStrategy,
        stringStrategy = stringStrategy,
        collectionStrategy = collectionStrategy,
        seed = seed,
        factories = _factories.toMap()
    )
}
