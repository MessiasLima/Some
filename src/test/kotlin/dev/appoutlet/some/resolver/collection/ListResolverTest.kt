package dev.appoutlet.some.resolver.collection

import dev.appoutlet.some.config.DefaultStrategyProvider
import dev.appoutlet.some.core.Strategy
import dev.appoutlet.some.test.defaultTestChain
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ListResolverTest {
    @Test
    fun `ListResolver generates list with correct size`() {
        val strategyProvider = DefaultStrategyProvider(
            mapOf(CollectionStrategy::class to CollectionStrategy(3..5))
        )
        val resolver = ListResolver(strategyProvider, Random.Default)

        val result = resolver.resolve(typeOf<List<String>>(), defaultTestChain)
        assertIs<List<*>>(result)
        assertTrue(result.size in 3..5)
    }

    @Test
    fun `ListResolver generates MutableList when requested`() {
        val resolver = ListResolver(DefaultStrategyProvider(), Random.Default)

        val result = resolver.resolve(typeOf<MutableList<String>>(), defaultTestChain)
        assertIs<MutableList<*>>(result)
    }

    @Test
    fun `ListResolver canResolve detects List types`() {
        val resolver = ListResolver(DefaultStrategyProvider(), Random.Default)
        assertTrue(resolver.canResolve(typeOf<List<String>>()))
    }

    @Test
    fun `ListResolver rejects non-List types`() {
        val resolver = ListResolver(DefaultStrategyProvider(), Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
    }
}
