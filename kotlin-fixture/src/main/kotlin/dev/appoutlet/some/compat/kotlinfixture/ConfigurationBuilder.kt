package dev.appoutlet.some.compat.kotlinfixture

import dev.appoutlet.some.core.Resolver
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Builder of [Configuration]. When [initial] is supplied it is built upon, allowing the various options to be set and
 * overridden.
 */
@Suppress("TooManyFunctions")
class ConfigurationBuilder(initial: Configuration = Configuration()) {

    /**
     * Add and remove [Resolver] to the resolver chain.
     *
     * We ask each [Resolver] if it handles the current input object, and it returns either a generated fixture or
     * `null`.
     */
    var resolvers: MutableList<Resolver> = initial.resolvers.toMutableList()

    /**
     * Providing a seeded random.
     *
     * By default, we generate unique values between runs using a default Random class. If you want repeatability you
     * can specify a seeded Random instance.
     *
     * ```
     * val fixture = kotlinFixture {
     *     random = Random(seed = 10)
     * }
     *
     * val alwaysTheSame = fixture<Int>()
     * ```
     */
    var random: Random = initial.random

    /**
     * Strategy controlling how nullable types are generated.
     */
    var nullabilityStrategy: NullabilityStrategy =
        initial.strategies[NullabilityStrategy::class] as? NullabilityStrategy
            ?: NullabilityStrategy.NeverNullStrategy

    /**
     * Strategy controlling whether constructor parameters with default values use the default or a generated value.
     */
    var optionalStrategy: OptionalStrategy =
        initial.strategies[OptionalStrategy::class] as? OptionalStrategy
            ?: OptionalStrategy.AlwaysOptionalStrategy

    /**
     * Strategy controlling how circular references are handled.
     */
    var recursionStrategy: RecursionStrategy =
        initial.strategies[RecursionStrategy::class] as? RecursionStrategy
            ?: RecursionStrategy.NullRecursionStrategy

    private var repeatCount: () -> Int = initial.repeatCount
    private val propertiesRepeatCount: MutableMap<KClass<*>, MutableMap<String, () -> Int>> =
        initial.propertiesRepeatCount.mapValues { it.value.toMutableMap() }.toMutableMap()
    private val properties: MutableMap<KClass<*>, MutableMap<String, GeneratorFun>> =
        initial.properties.mapValues { it.value.toMutableMap() }.toMutableMap()
    private val factories: MutableMap<KType, GeneratorFun> = initial.factories.toMutableMap()
    private val subTypes: MutableMap<KClass<*>, KClass<*>> = initial.subTypes.toMutableMap()

    internal val strategies: MutableMap<KClass<*>, Any> = initial.strategies.toMutableMap()

    /**
     * Setting list and map length with `repeatCount`.
     *
     * Used to determine the length used for lists and maps. By default, the library generates 5 items.
     *
     * ```
     * val fixture = kotlinFixture {
     *     repeatCount { 3 }
     * }
     *
     * val listOfThreeItems = fixture<List<Int>>()
     * ```
     *
     * `repeatCount` is a factory method so can be used to return lists and maps of different lengths each execution:
     *
     * ```
     * repeatCount {
     *     random.nextInt(1, 5)
     * }
     * ```
     */
    fun repeatCount(generator: () -> Int) {
        repeatCount = generator
    }

    /**
     * Overrides the length used for lists and maps of the property [propertyName] declared by [T].
     */
    @Suppress("UNCHECKED_CAST", "DEPRECATION_ERROR")
    inline fun <reified T> repeatCount(
        propertyName: String,
        noinline generator: () -> Int
    ) = repeatCount(T::class, propertyName, generator)

    /**
     * Overrides the length used for lists and maps of [property].
     */
    inline fun <reified T, G> repeatCount(
        property: KProperty1<T, G>,
        noinline generator: () -> Int
    ) {
        if (property !is KMutableProperty1) {
            val constructorParams = T::class.constructors.flatMap {
                it.parameters.map(KParameter::name)
            }

            check(constructorParams.contains(property.name)) {
                "No setter available for ${T::class.qualifiedName}.${property.name}"
            }
        }

        @Suppress("UNCHECKED_CAST", "DEPRECATION_ERROR")
        repeatCount(T::class, property.name, generator)
    }

    /**
     * Overrides the length used for lists and maps of the property backing the setter [function].
     */
    @Suppress("UNCHECKED_CAST", "DEPRECATION_ERROR")
    fun repeatCount(
        function: KFunction<Unit>,
        generator: () -> Int
    ) = repeatCount(
        function.parameters[0].type.classifier as KClass<*>,
        function.name,
        generator
    )

    @Deprecated(
        "Use one of the repeatCount(Class::property) { … }, repeatCount<Class>(propertyName) { … } or " +
                "repeatCount<Property>(Class::function) { … } functions",
        level = DeprecationLevel.ERROR
    )
    fun repeatCount(clazz: KClass<*>, propertyName: String, generator: () -> Int) {
        val classProperties = propertiesRepeatCount.getOrElse(clazz) { mutableMapOf() }
        classProperties[propertyName] = generator

        propertiesRepeatCount[clazz] = classProperties
    }

    /**
     * Customising class generation with factory.
     *
     * Used to return the given instance for a particular class using a factory method.
     *
     * ```
     * val fixture = kotlinFixture {
     *     factory<Number> { 41 }
     * }
     *
     * val alwaysFortyOne = fixture<Number>()
     * ```
     *
     * As `factory` is a factory method you can return different values on every execution:
     *
     * ```
     * factory<Number> {
     *     random.nextInt(10, 50)
     * }
     * ```
     *
     * #### Generating values in a `range`
     *
     * `factory` has a built-in `range` function to make it easy to generate values in a range.
     *
     * ```
     * factory<Int> { range(1..10) }
     * ```
     */
    @Suppress("UNCHECKED_CAST", "DEPRECATION_ERROR")
    inline fun <reified T> factory(noinline generator: Generator<T>.() -> T) =
        factory(typeOf<T>(), generator as GeneratorFun)

    @Deprecated("Use the factory<Class> { … } function", level = DeprecationLevel.ERROR)
    fun factory(type: KType, generator: GeneratorFun) {
        factories[type] = generator
    }

    /**
     * Resolving abstract superclasses to a chosen subclass with `subType`.
     *
     * Used to always return an instance of a particular subclass for a superclass.
     *
     * ```
     * val fixture = kotlinFixture {
     *     subType<Number, Int>()
     * }
     *
     * val alwaysInt = fixture<Number>()
     * ```
     */
    @Suppress("DEPRECATION_ERROR")
    inline fun <reified T, reified U : T> subType() = subType(T::class, U::class)

    @Deprecated(
        "Use the subType<Superclass, Subclass>() function instead",
        level = DeprecationLevel.ERROR
    )
    fun subType(superType: KClass<*>, subType: KClass<*>) {
        subTypes[superType] = subType
    }

    /**
     * Customising generation of class properties with property.
     *
     * Used to override constructor parameters or mutable properties when generating instances of generic classes.
     *
     * ```
     * val fixture = kotlinFixture {
     *     // Public constructor parameters overridden by reference:
     *     property(KotlinClass::readOnly) { "a" }
     *
     *     // Private constructor parameters are overridden by name:
     *     property<KotlinClass, String>("private") { "b" }
     *
     *     // Public member properties overridden by reference:
     *     property(KotlinClass::member) { "c" }
     * }
     * ```
     */
    @Suppress("UNCHECKED_CAST", "DEPRECATION_ERROR")
    inline fun <reified T, G> property(
        propertyName: String,
        noinline generator: Generator<G>.() -> G
    ) = property(T::class, propertyName, generator as GeneratorFun)

    /**
     * Customising generation of class properties with property.
     *
     * Used to override constructor parameters or mutable properties when generating instances of generic classes.
     *
     * ```
     * val fixture = kotlinFixture {
     *     property(KotlinClass::readOnly) { "a" }
     * }
     * ```
     */
    inline fun <reified T, G> property(
        property: KProperty1<T, G>,
        noinline generator: Generator<G>.() -> G
    ) {
        if (property !is KMutableProperty1) {
            val constructorParams = T::class.constructors.flatMap {
                it.parameters.map(KParameter::name)
            }

            check(constructorParams.contains(property.name)) {
                "No setter available for ${T::class.qualifiedName}.${property.name}"
            }
        }

        @Suppress("UNCHECKED_CAST", "DEPRECATION_ERROR")
        property(T::class, property.name, generator as GeneratorFun)
    }

    /**
     * Customising generation of class properties with property.
     *
     * Used to override constructor parameters or mutable properties when generating instances of generic classes.
     *
     * ```
     * val fixture = kotlinFixture {
     *     property<String>(JavaClass::setMutable) { "d" }
     * }
     * ```
     */
    @Suppress("UNCHECKED_CAST", "DEPRECATION_ERROR")
    inline fun <reified G> property(
        function: KFunction<Unit>,
        noinline generator: Generator<G>.() -> G
    ) = property(
        function.parameters[0].type.classifier as KClass<*>,
        function.name,
        generator as GeneratorFun
    )

    @Deprecated(
        "Use one of the property(Class::property) { … }, property<Class, Property>(propertyName) { … } or " +
                "property<Property>(Class::function) { … } functions",
        level = DeprecationLevel.ERROR
    )
    fun property(clazz: KClass<*>, propertyName: String, generator: GeneratorFun) {
        val classProperties = properties.getOrElse(clazz) { mutableMapOf() }
        classProperties[propertyName] = generator

        properties[clazz] = classProperties
    }

    fun build() = Configuration(
        repeatCount = repeatCount,
        propertiesRepeatCount = propertiesRepeatCount.mapValues { it.value.toMap() }.toMap(),
        properties = properties.mapValues { it.value.toMap() }.toMap(),
        factories = factories.toMap(),
        subTypes = subTypes.toMap(),
        random = random,
        resolvers = resolvers.toList(),
        strategies = strategies.toMutableMap().apply {
            this[NullabilityStrategy::class] = nullabilityStrategy
            this[OptionalStrategy::class] = optionalStrategy
            this[RecursionStrategy::class] = recursionStrategy
        }.toMap(),
    )
}
