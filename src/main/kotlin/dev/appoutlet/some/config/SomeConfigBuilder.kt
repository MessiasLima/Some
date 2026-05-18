package dev.appoutlet.some.config

import dev.appoutlet.some.core.FixtureContext
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.instanceParameter

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
    private val _propertyFactories: MutableMap<Pair<KClass<*>, String>, FixtureContext.() -> Any?> = mutableMapOf()

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
     * Registers a custom factory function for a specific property of class [T].
     *
     * @param T The class containing the property.
     * @param V The type of the property.
     * @param property The [KProperty1] representing the property to override.
     * @param factory Lambda receiving a [FixtureContext] and returning a value of type [V].
     */
    fun <T : Any, V> property(property: KProperty1<T, V>, factory: FixtureContext.() -> V) {
        val kClass = property.instanceParameter?.type?.classifier as? KClass<*>
            ?: error("Could not determine class for property ${property.name}")
        _propertyFactories[kClass to property.name] = factory
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
     * Populates the builder's property factory map with entries from an existing map.
     *
     * Used internally by [SomeConfig.toBuilder] to transfer property factory registrations.
     *
     * @param propertyFactories Map of property factory registrations to copy into this builder.
     */
    internal fun populatePropertyFactories(
        propertyFactories: Map<Pair<KClass<*>, String>, FixtureContext.() -> Any?>
    ) {
        _propertyFactories.putAll(propertyFactories)
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
        factories = _factories.toMap(),
        propertyFactories = _propertyFactories.toMap()
    )
}
