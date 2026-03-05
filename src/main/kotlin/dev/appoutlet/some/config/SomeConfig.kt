package dev.appoutlet.some.config

import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.ResolverChain
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
import dev.appoutlet.some.resolver.DurationResolver
import dev.appoutlet.some.resolver.EnumResolver
import dev.appoutlet.some.resolver.FloatResolver
import dev.appoutlet.some.resolver.InstantResolver
import dev.appoutlet.some.resolver.IntResolver
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
import dev.appoutlet.some.resolver.UuidResolver
import dev.appoutlet.some.resolver.ValueClassResolver
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

    internal fun buildChain(): ResolverChain {
        val resolvers = listOf(
            CustomFactoryResolver(factories),
            NullableResolver(),
            ObjectResolver(),
            EnumResolver(),
            SealedClassResolver(),
            ValueClassResolver(),
            StringResolver(),
            IntResolver(),
            LongResolver(),
            DoubleResolver(),
            FloatResolver(),
            BooleanResolver(),
            CharResolver(),
            ByteResolver(),
            ShortResolver(),
            UuidResolver(),
            BigDecimalResolver(),
            BigIntegerResolver(),
            LocalDateResolver(),
            LocalDateTimeResolver(),
            InstantResolver(),
            DurationResolver(),
            ListResolver(),
            SetResolver(),
            MapResolver(),
            ArrayResolver(),
            DataClassResolver()
        )
        return ResolverChain(resolvers)
    }

    internal fun buildRandom(): Random = seed?.let { Random(it) } ?: Random.Default
}
