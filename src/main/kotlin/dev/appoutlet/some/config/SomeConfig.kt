package dev.appoutlet.some.config

import dev.appoutlet.some.core.FixtureContext
import kotlin.reflect.KClass
import kotlin.random.Random

class SomeConfig {
    var nullableStrategy: NullableStrategy = NullableStrategy.Random()
    var stringStrategy: StringStrategy = StringStrategy.Random
    var collectionStrategy: CollectionStrategy = CollectionStrategy()
    var seed: Long? = null

    private val factories = mutableMapOf<KClass<*>, FixtureContext.() -> Any?>()

    fun <T : Any> register(kClass: KClass<T>, factory: FixtureContext.() -> T) {
        factories[kClass] = factory
    }

    internal fun buildChain(): dev.appoutlet.some.core.ResolverChain {
        val resolvers = listOf<dev.appoutlet.some.core.TypeResolver>(
            dev.appoutlet.some.resolver.CustomFactoryResolver(factories),
            dev.appoutlet.some.resolver.NullableResolver(),
            dev.appoutlet.some.resolver.ObjectResolver(),
            dev.appoutlet.some.resolver.EnumResolver(),
            dev.appoutlet.some.resolver.SealedClassResolver(),
            dev.appoutlet.some.resolver.ValueClassResolver(),
            dev.appoutlet.some.resolver.StringResolver(),
            dev.appoutlet.some.resolver.IntResolver(),
            dev.appoutlet.some.resolver.LongResolver(),
            dev.appoutlet.some.resolver.DoubleResolver(),
            dev.appoutlet.some.resolver.FloatResolver(),
            dev.appoutlet.some.resolver.BooleanResolver(),
            dev.appoutlet.some.resolver.CharResolver(),
            dev.appoutlet.some.resolver.ByteResolver(),
            dev.appoutlet.some.resolver.ShortResolver(),
            dev.appoutlet.some.resolver.UuidResolver(),
            dev.appoutlet.some.resolver.BigDecimalResolver(),
            dev.appoutlet.some.resolver.BigIntegerResolver(),
            dev.appoutlet.some.resolver.LocalDateResolver(),
            dev.appoutlet.some.resolver.LocalDateTimeResolver(),
            dev.appoutlet.some.resolver.InstantResolver(),
            dev.appoutlet.some.resolver.DurationResolver(),
            dev.appoutlet.some.resolver.ListResolver(),
            dev.appoutlet.some.resolver.SetResolver(),
            dev.appoutlet.some.resolver.MapResolver(),
            dev.appoutlet.some.resolver.ArrayResolver(),
            dev.appoutlet.some.resolver.DataClassResolver()
        )
        return dev.appoutlet.some.core.ResolverChain(resolvers)
    }

    internal fun buildRandom(): Random = seed?.let { Random(it) } ?: Random.Default
}
