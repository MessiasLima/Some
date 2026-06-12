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
    var nullableStrategy: NullableStrategy
        get() = strategiesMap[NullableStrategy::class] as NullableStrategy
        set(value) { strategiesMap[NullableStrategy::class] = value }

    /**
     * Strategy for generating string values.
     * Defaults to [StringStrategy.Random].
     */
    var stringStrategy: StringStrategy
        get() = strategiesMap[StringStrategy::class] as StringStrategy
        set(value) { strategiesMap[StringStrategy::class] = value }

    /**
     * Strategy for generating collection sizes.
     * Defaults to [CollectionStrategy] with a range of 1..5.
     */
    var collectionStrategy: CollectionStrategy
        get() = strategiesMap[CollectionStrategy::class] as CollectionStrategy
        set(value) { strategiesMap[CollectionStrategy::class] = value }

    /**
     * Strategy for handling data class constructor defaults.
     * Defaults to [DefaultValueStrategy.UseDefault].
     */
    var defaultValueStrategy: DefaultValueStrategy = DefaultValueStrategy.UseDefault

    /**
     * Seed for reproducible random generation.
     * If null, uses [kotlin.random.Random.Default].
     */
    var seed: Long? = null

    @PublishedApi
    internal val strategiesMap: MutableMap<KClass<out Strategy>, Strategy> = mutableMapOf(
        NullableStrategy::class to NullableStrategy.NullOnCircularReference,
        StringStrategy::class to StringStrategy.Random(),
        CollectionStrategy::class to CollectionStrategy(),
    )
    private val _typeFactories: MutableMap<KClass<*>, FixtureContext.() -> Any?> = mutableMapOf()
    private val _propertyFactories: MutableMap<Pair<KClass<*>, String>, FixtureContext.() -> Any?> = mutableMapOf()

    /**
     * Registers a strategy instance.
     *
     * @param T The strategy type.
     * @param strategy The strategy instance to register.
     */
    inline fun <reified T : Strategy> strategy(strategy: T) {
        strategiesMap[T::class] = strategy
    }

    /**
     * Registers a custom type factory function for type [T].
     *
     * Custom type factories take priority over built-in resolvers.
     *
     * @param T The type this type factory produces.
     * @param kClass The [KClass] of the type to override.
     * @param typeFactory Lambda receiving a [FixtureContext] and returning a value of type [T].
     */
    fun <T : Any> factory(kClass: KClass<T>, typeFactory: FixtureContext.() -> T) {
        _typeFactories[kClass] = typeFactory
    }

    /**
     * Deprecated: Use [factory] instead.
     */
    @Deprecated("Use factory() instead", ReplaceWith("factory(kClass, typeFactory)"))
    fun <T : Any> register(kClass: KClass<T>, typeFactory: FixtureContext.() -> T) {
        factory(kClass, typeFactory)
    }

    /**
     * Registers a custom property factory function for a specific property of class [T].
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
     * Populates the builder's strategy map with entries from an existing map.
     *
     * Used internally by [SomeConfig.toBuilder] to transfer strategy registrations.
     *
     * @param strategies Map of strategy registrations to copy into this builder.
     */
    internal fun populateStrategies(strategies: Map<KClass<out Strategy>, Strategy>) {
        strategiesMap.putAll(strategies)
    }

    /**
     * Populates the builder's type factory map with entries from an existing map.
     *
     * Used internally by [SomeConfig.toBuilder] to transfer type factory registrations.
     *
     * @param typeFactories Map of type factory registrations to copy into this builder.
     */
    internal fun populateTypeFactories(typeFactories: Map<KClass<*>, FixtureContext.() -> Any?>) {
        _typeFactories.putAll(typeFactories)
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
        strategies = strategiesMap.toMap(),
        defaultValueStrategy = defaultValueStrategy,
        seed = seed,
        typeFactories = _typeFactories.toMap(),
        propertyFactories = _propertyFactories.toMap()
    )
}
