package dev.appoutlet.some

import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.resolver.ClassResolver
import dev.appoutlet.some.resolver.CustomTypeFactoryResolver
import dev.appoutlet.some.resolver.NullableResolver
import dev.appoutlet.some.resolver.ObjectResolver
import dev.appoutlet.some.resolver.StringResolver
import dev.appoutlet.some.test.DiscoveredType
import dev.appoutlet.some.test.TestTypeResolver
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ServiceLoaderDiscoveryTest {
    @Test
    fun `top-level some discovers third-party resolver`() {
        val result: DiscoveredType = some()
        assertEquals("discovered", result.value)
    }

    @Test
    fun `someSetup discovers third-party resolver`() {
        val generator = someSetup { }
        val result: DiscoveredType = generator()
        assertEquals("discovered", result.value)
    }

    @Test
    fun `custom factory takes precedence over discovered resolver`() {
        val generator = someSetup {
            factory(DiscoveredType::class) { DiscoveredType("custom") }
        }
        val result: DiscoveredType = generator()
        assertEquals("custom", result.value)
    }

    @Test
    fun `discovered resolvers take precedence over built-in resolvers`() {
        val resolvers = SomeConfig().buildResolvers()
        val resolverClasses = resolvers.map { it::class }

        assertTrue(resolverClasses.first() == CustomTypeFactoryResolver::class)
        assertTrue(resolverClasses.last() == ClassResolver::class)
        assertTrue(TestTypeResolver::class in resolverClasses)

        val nullableResolverIndex = resolverClasses.indexOf(NullableResolver::class)
        val objectResolverIndex = resolverClasses.indexOf(ObjectResolver::class)
        val stringResolverIndex = resolverClasses.indexOf(StringResolver::class)
        val classResolverIndex = resolverClasses.indexOf(ClassResolver::class)
        val discoveredIndex = resolverClasses.indexOf(TestTypeResolver::class)

        assertTrue(nullableResolverIndex < discoveredIndex)
        assertTrue(discoveredIndex < objectResolverIndex)
        assertTrue(discoveredIndex < stringResolverIndex)
        assertTrue(discoveredIndex < classResolverIndex)
    }

    @Test
    fun `misbehaving provider is skipped and built-in chain still works`() {
        val result: Int = some()
        assertTrue(result in Int.MIN_VALUE..Int.MAX_VALUE)
    }

    @Test
    fun `valid providers survive when another provider fails during loading`() {
        val result: DiscoveredType = some()
        assertEquals("discovered", result.value)
    }
}
