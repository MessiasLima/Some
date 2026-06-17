package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.DefaultStrategyProvider
import dev.appoutlet.some.test.defaultTestChain
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

data class CustomFactoryClass(val value: String)

class CustomTypeFactoryResolverTest {
    @Test
    fun `CustomTypeFactoryResolver canResolve detects registered types`() {
        val resolver = CustomTypeFactoryResolver(
            DefaultStrategyProvider(),
            mapOf(CustomFactoryClass::class to { CustomFactoryClass("test") }),
            Random.Default
        )
        assertTrue(resolver.canResolve(typeOf<CustomFactoryClass>()))
    }

    @Test
    fun `CustomTypeFactoryResolver canResolve rejects unregistered types`() {
        val resolver = CustomTypeFactoryResolver(
            DefaultStrategyProvider(),
            mapOf(CustomFactoryClass::class to { CustomFactoryClass("test") }),
            Random.Default
        )
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
    }

    @Test
    fun `CustomTypeFactoryResolver resolve returns factory result`() {
        val resolver = CustomTypeFactoryResolver(
            DefaultStrategyProvider(),
            mapOf(CustomFactoryClass::class to { CustomFactoryClass("from-factory") }),
            Random.Default
        )
        val result = resolver.resolve(typeOf<CustomFactoryClass>(), defaultTestChain)
        assertNotNull(result)
        assertTrue(result is CustomFactoryClass)
    }
}
