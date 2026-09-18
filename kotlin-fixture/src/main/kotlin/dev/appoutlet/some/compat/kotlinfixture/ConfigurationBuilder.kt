package dev.appoutlet.some.compat.kotlinfixture

import kotlin.random.Random
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter
import kotlin.reflect.typeOf

/**
 * DSL builder for compatibility fixture configuration.
 */
class ConfigurationBuilder internal constructor(initial: Configuration = Configuration()) {
    var random: Random = initial.random
    var nullabilityStrategy: NullabilityStrategy = initial.nullabilityStrategy
    var optionalStrategy: OptionalStrategy = initial.optionalStrategy
    var recursionStrategy: RecursionStrategy = initial.recursionStrategy

    @PublishedApi
    internal val typeFactories = initial.typeFactories.toMutableMap()

    @PublishedApi
    internal val propertyFactories = initial.propertyFactories.toMutableMap()

    @PublishedApi
    internal val subTypes = initial.subTypes.toMutableMap()

    @PublishedApi
    internal val filters = initial.filters.toMutableMap()

    /**
     * Registers a custom type factory for type [T].
     */
    inline fun <reified T> factory(noinline factory: Generator<T>.() -> T) {
        @Suppress("UNCHECKED_CAST")
        typeFactories[typeOf<T>()] = factory as Generator<Any?>.() -> Any?
    }

    /**
     * Registers a custom type factory for [type].
     */
    fun factory(type: KType, factory: Generator<Any?>.() -> Any?) {
        typeFactories[type] = factory
    }

    /**
     * Registers a custom property factory for [property].
     */
    fun <T : Any, V> property(property: KProperty1<T, V>, factory: Generator<V>.() -> V) {
        val (ownerClass, name) = extractOwnerAndName(property)
        @Suppress("UNCHECKED_CAST")
        propertyFactories[ownerClass to name] = factory as Generator<Any?>.() -> Any?
    }

    /**
     * Registers a custom property factory for [property].
     */
    fun <V> property(property: KProperty<V>, factory: Generator<V>.() -> V) {
        val (ownerClass, name) = extractOwnerAndName(property)
        @Suppress("UNCHECKED_CAST")
        propertyFactories[ownerClass to name] = factory as Generator<Any?>.() -> Any?
    }

    /**
     * Maps superclass [Super] to subtype [Sub].
     */
    inline fun <reified Super : Any, reified Sub : Super> subType() {
        subType(Super::class, Sub::class)
    }

    /**
     * Maps [superType] to [subType].
     */
    fun subType(superType: KClass<*>, subType: KClass<*>) {
        subTypes[superType] = subType
    }

    /**
     * Configures a filter for type [T].
     */
    inline fun <reified T> filter(noinline configuration: FilterBuilder<T>.() -> Unit) {
        filter(typeOf<T>(), configuration)
    }

    /**
     * Configures a filter for [type].
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> filter(type: KType, configuration: FilterBuilder<T>.() -> Unit) {
        val builder = FilterBuilder<T>().apply(configuration)
        filters[type] = builder.build() as FilterSpec<Any?>
    }

    internal fun build(): Configuration = Configuration(
        random = random,
        typeFactories = typeFactories.toMap(),
        propertyFactories = propertyFactories.toMap(),
        subTypes = subTypes.toMap(),
        filters = filters.toMap(),
        nullabilityStrategy = nullabilityStrategy,
        optionalStrategy = optionalStrategy,
        recursionStrategy = recursionStrategy,
    )

    private fun extractOwnerAndName(property: KProperty<*>): Pair<KClass<*>, String> {
        val propertyName = property.name

        val instanceParamClass = (property as? KCallable<*>)?.parameters
            ?.firstOrNull { it.kind == KParameter.Kind.INSTANCE || it.kind == KParameter.Kind.EXTENSION_RECEIVER }
            ?.type?.classifier as? KClass<*>

        val ownerClass = instanceParamClass
            ?: (property as? KProperty1<*, *>)?.javaGetter?.declaringClass?.kotlin
            ?: (property as? KProperty1<*, *>)?.javaField?.declaringClass?.kotlin

        requireNotNull(ownerClass) {
            "Could not determine declaring class for property '${property.name}'. " +
                "Unsupported private or non-constructor property."
        }

        return ownerClass to propertyName
    }
}
