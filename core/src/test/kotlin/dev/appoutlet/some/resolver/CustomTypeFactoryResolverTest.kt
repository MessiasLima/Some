package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.DefaultStrategyProvider
import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.test.defaultTestChain
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

data class CustomFactoryClass(val value: String)

class CustomTypeFactoryResolverTest {
    @Test
    fun `CustomTypeFactoryResolver canResolve detects registered types`() {
        val resolver = CustomFactoryResolver(
            DefaultStrategyProvider(),
            mapOf(CustomFactoryClass::class to { CustomFactoryClass("test") }),
            Random.Default
        )
        assertTrue(resolver.canResolve(typeOf<CustomFactoryClass>()))
    }

    @Test
    fun `CustomTypeFactoryResolver canResolve rejects unregistered types`() {
        val resolver = CustomFactoryResolver(
            DefaultStrategyProvider(),
            mapOf(CustomFactoryClass::class to { CustomFactoryClass("test") }),
            Random.Default
        )
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
    }

    @Test
    fun `CustomTypeFactoryResolver resolve returns factory result`() {
        val resolver = CustomFactoryResolver(
            DefaultStrategyProvider(),
            mapOf(CustomFactoryClass::class to { CustomFactoryClass("from-factory") }),
            Random.Default
        )
        val result = resolver.resolve(typeOf<CustomFactoryClass>(), defaultTestChain)
        assertNotNull(result)
        assertTrue(result is CustomFactoryClass)
    }

    @Test
    fun `CustomTypeFactoryResolver resolve returns null for an unregistered type`() {
        val resolver = CustomFactoryResolver(
            DefaultStrategyProvider(),
            emptyMap(),
            Random.Default
        )

        assertNull(resolver.resolve(typeOf<String>(), defaultTestChain))
    }

    @Test
    fun `CustomTypeFactoryResolver matches an exact generic type`() {
        val resolver = CustomFactoryResolver(
            DefaultStrategyProvider(),
            emptyMap(),
            Random.Default,
            mapOf(typeOf<List<Number>>() to { listOf<Number>(1, 2) }),
        )

        assertTrue(resolver.canResolve(typeOf<List<Number>>()))
        assertEquals(listOf<Number>(1, 2), resolver.resolve(typeOf<List<Number>>(), defaultTestChain))
    }

    @Test
    fun `CustomTypeFactoryResolver rejects a different generic instantiation`() {
        val resolver = createResolver(
            kTypeFactories = mapOf(typeOf<List<Number>>() to { listOf<Number>(1, 2) }),
        )

        assertFalse(resolver.canResolve(typeOf<List<String>>()))
        assertNull(resolver.resolve(typeOf<List<String>>(), defaultTestChain))
    }

    @Test
    fun `CustomTypeFactoryResolver supports a non-null fallback for nullable requests`() {
        val resolver = createResolver(
            kTypeFactories = mapOf(typeOf<List<Number>>() to { listOf<Number>(1, 2) }),
        )

        assertTrue(resolver.canResolve(typeOf<List<Number>?>()))
        assertEquals(listOf<Number>(1, 2), resolver.resolve(typeOf<List<Number>?>(), defaultTestChain))
    }

    @Test
    fun `CustomTypeFactoryResolver prefers a type factory over a class factory`() {
        val resolver = createResolver(
            typeFactories = mapOf(List::class to { listOf("class-factory") }),
            kTypeFactories = mapOf(typeOf<List<Number>>() to { listOf<Number>(1, 2) }),
        )

        assertEquals(listOf<Number>(1, 2), resolver.resolve(typeOf<List<Number>>(), defaultTestChain))
        assertEquals(listOf("class-factory"), resolver.resolve(typeOf<List<String>>(), defaultTestChain))
    }

    private fun createResolver(
        typeFactories: Map<kotlin.reflect.KClass<*>, FixtureContext.() -> Any?> = emptyMap(),
        kTypeFactories: Map<KType, FixtureContext.() -> Any?> = emptyMap(),
    ) = CustomFactoryResolver(
        strategyProvider = DefaultStrategyProvider(),
        typeFactories = typeFactories,
        random = Random.Default,
        kTypeFactories = kTypeFactories,
    )
}
