package dev.appoutlet.some.config

import dev.appoutlet.some.core.FixtureContext
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.typeOf

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
 *     strategy(FloatStrategy(0.0f..10.0f))
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
 *     factory<List<Int>> { listOf(1, 2, 3) }
 * }
 * ```
 *
 * A class-level factory registered with a [KClass] applies to every generic instantiation and nullable variant of
 * that class. A type-level factory registered with a [KType] applies only to the requested type, and takes precedence
 * over a class-level factory for the same classifier.
 */
@Suppress("TooManyFunctions")
class SomeConfigBuilder {
    private val _strategies: MutableMap<KClass<out Strategy>, Strategy> = mutableMapOf()

    /**
     * Seed for reproducible random generation.
     * If null, uses [kotlin.random.Random.Default].
     */
    var seed: Long? = null

    private val _typeFactories = mutableMapOf<KClass<*>, FixtureContext.() -> Any?>()
    private val _kTypeFactories = mutableMapOf<KType, FixtureContext.() -> Any?>()
    private val _propertyFactories = mutableMapOf<Pair<KClass<*>, String>, FixtureContext.() -> Any?>()
    private val _subTypes = mutableMapOf<KClass<*>, KClass<*>>()

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
     * Registers a custom type factory function for the exact reified type [T].
     *
     * The factory is registered against `typeOf<T>()`, so generic arguments and nullability are part of the match.
     * A factory registered for `List<Number>` does not apply to `List<String>`; it does apply to `List<Number>?` when
     * the nullable request resolves to a non-null value. Type-level factories take precedence over class-level
     * factories registered with [factory].
     *
     * @param T The exact type this factory produces.
     * @param typeFactory Lambda receiving a [FixtureContext] and returning a value of type [T].
     */
    inline fun <reified T> factory(noinline typeFactory: FixtureContext.() -> T) {
        factory(typeOf<T>(), typeFactory)
    }

    /**
     * Registers a custom type factory function for the exact [KType].
     *
     * @param kType The exact type to override, including generic arguments and nullability.
     * @param typeFactory Lambda receiving a [FixtureContext] and returning a value assignable to [kType].
     */
    fun <T> factory(kType: KType, typeFactory: FixtureContext.() -> T) {
        @Suppress("UNCHECKED_CAST")
        _kTypeFactories[kType] = typeFactory as FixtureContext.() -> Any?
    }

    /**
     * Registers a concrete subtype to generate whenever the parent type [T] is requested.
     *
     * This mapping is deterministic and applies to sealed types, abstract classes, and non-sealed interfaces. The
     * subtype is resolved through the normal resolver chain, so its properties are generated as usual. A subtype
     * mapping is considered after custom type factories and before random sealed-class selection.
     *
     * Self-mappings and cyclic mappings fail during resolution with
     * [dev.appoutlet.some.exception.SomeSubtypeMappingException].
     *
     * @param T The parent type to map from.
     * @param U The concrete subtype to generate. Must be assignable to [T].
     */
    inline fun <reified T : Any, reified U : T> subType() {
        subType(T::class, U::class)
    }

    /**
     * Registers a concrete subtype for [parent].
     *
     * @param parent The parent class to map from.
     * @param subtype The concrete subtype to generate for [parent].
     */
    fun <T : Any> subType(parent: KClass<T>, subtype: KClass<out T>) {
        _subTypes[parent] = subtype
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
     * Populates the builder's type-level factory map with entries from an existing map.
     *
     * Used internally by [SomeConfig.toBuilder] to transfer type-level factory registrations.
     *
     * @param kTypeFactories Map of type-level factory registrations to copy into this builder.
     */
    internal fun populateKTypeFactories(kTypeFactories: Map<KType, FixtureContext.() -> Any?>) {
        _kTypeFactories.putAll(kTypeFactories)
    }

    /**
     * Populates the builder's subtype mapping with entries from an existing map.
     *
     * Used internally by [SomeConfig.toBuilder] to transfer subtype registrations.
     *
     * @param subTypes Map of subtype registrations to copy into this builder.
     */
    internal fun populateSubTypes(subTypes: Map<KClass<*>, KClass<*>>) {
        _subTypes.putAll(subTypes)
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
        strategies = _strategies.toMap(),
        seed = seed,
        typeFactories = _typeFactories.toMap(),
        propertyFactories = _propertyFactories.toMap(),
        kTypeFactories = _kTypeFactories.toMap(),
        subTypes = _subTypes.toMap()
    )
}

fun buildSomeConfig(config: SomeConfigBuilder.() -> Unit = {}): SomeConfig =
    SomeConfigBuilder().apply(config).build()
