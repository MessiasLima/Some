package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.TypeResolver
import kotlin.reflect.typeOf
import kotlin.test.Test

class ResolverChainDebug {
    @Test
    fun `debug resolver chain`() {
        val resolvers = listOf(
            dev.appoutlet.some.resolver.CustomFactoryResolver(emptyMap()),
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
        
        val nullableType = typeOf<String?>()
        
        for (resolver in resolvers) {
            println("${resolver.javaClass.simpleName}.canResolve($nullableType) = ${resolver.canResolve(nullableType)}")
        }
    }
}
