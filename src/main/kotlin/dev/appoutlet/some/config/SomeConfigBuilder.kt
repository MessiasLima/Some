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
 *
 * ## Strategy registration
 *
 * Override built-in strategies or register custom ones using the [strategy] method:
 *
 * ```kotlin
 * someSetup {
 *     strategy(NullableStrategy.NeverNull)
 *     strategy(StringStrategy.Uuid)
 *     strategy(CollectionStrategy(5..10))
 * }
 * ```
 *
 * ## Factory registration
 *
 * Override type resolution with [factory]:
 *
 * ```kotlin
 * someSetup {
 *     factory(String::class) { "fixed-value" }
 * }
 * ```
 */
class SomeConfigBuilder {
    private val _strategies: MutableMap<KClass<out Strategy>, Strategy> = mutableMapOf()

    /**
     * Seed for reproducible random generation.
     * If null, uses [kotlin.random.Random.Default].
     */
    var seed: Long? = null

    private val _typeFactories = mutableMapOf<KClass<*>, FixtureContext.() -> Any?>()
    private val _propertyFactories = mutableMapOf<Pair<KClass<*>, String>, FixtureContext.() -> Any?>()

    /**
     * Registers a strategy for fixture generation.
     *
     * The strategy replaces any previous strategy registered under the same base type.
     * When no strategy is explicitly registered, each resolver falls back to its own
     * sensible default (e.g. [NullableStrategy.NullOnCircularReference],
     * [StringStrategy.Random], [CollectionStrategy] with a range of 1..5).
     *
     * @param strategy The strategy instance to register.
     */
    infix fun strategy(strategy: Strategy) {
        _strategies[strategy.key] = strategy
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
        _strategies.putAll(strategies)
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
     * Strategies registered via [strategy] override the built-in defaults. Type and property
     * factories are copied as immutable maps.
     *
     * @return A new [SomeConfig] instance with the configured values.
     */
    fun build(): SomeConfig = SomeConfig(
        strategies = _strategies,
        seed = seed,
        typeFactories = _typeFactories.toMap(),
        propertyFactories = _propertyFactories.toMap()
    )
}

fun buildSomeConfig(config: SomeConfigBuilder.() -> Unit = {}): SomeConfig =
    SomeConfigBuilder().apply(config).build()
