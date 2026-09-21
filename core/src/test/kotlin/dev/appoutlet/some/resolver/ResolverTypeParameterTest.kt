package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.DefaultStrategyProvider
import kotlin.random.Random
import kotlin.reflect.full.createType
import kotlin.test.Test
import kotlin.test.assertFalse

private class ResolverTypeParameterContainer<T>

class ResolverTypeParameterTest {
    private val typeParameter = ResolverTypeParameterContainer::class.typeParameters.single().createType()

    @Test
    fun `resolvers reject a type parameter classifier`() {
        assertFalse(ArrayResolver(DefaultStrategyProvider(), Random.Default).canResolve(typeParameter))
        assertFalse(ClassResolver(DefaultStrategyProvider(), random = Random.Default).canResolve(typeParameter))
        assertFalse(
            CustomFactoryResolver(DefaultStrategyProvider(), emptyMap(), Random.Default).canResolve(typeParameter)
        )
        assertFalse(EnumResolver(Random.Default).canResolve(typeParameter))
        assertFalse(ListResolver(DefaultStrategyProvider(), Random.Default).canResolve(typeParameter))
        assertFalse(MapResolver(DefaultStrategyProvider(), Random.Default).canResolve(typeParameter))
        assertFalse(ObjectResolver().canResolve(typeParameter))
        assertFalse(OptionalResolver(DefaultStrategyProvider(), Random.Default).canResolve(typeParameter))
        assertFalse(SealedClassResolver(Random.Default).canResolve(typeParameter))
        assertFalse(SetResolver(DefaultStrategyProvider(), Random.Default).canResolve(typeParameter))
        assertFalse(ValueClassResolver().canResolve(typeParameter))
    }
}
