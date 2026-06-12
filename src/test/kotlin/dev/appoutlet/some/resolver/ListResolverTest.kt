package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.CollectionStrategy
import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.config.buildSomeConfig
import dev.appoutlet.some.core.ResolverChain
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
        val config = buildSomeConfig {
            strategy(CollectionStrategy(3..5))
        }
        val resolvers = config.buildResolvers()
        val resolver = ListResolver(CollectionStrategy(3..5), Random.Default)

        val result = resolver.resolve(typeOf<List<String>>(), ResolverChain(resolvers, config[NullableStrategy::class]))
        assertIs<List<*>>(result)
        assertTrue(result.size in 3..5)
    }

    @Test
    fun `ListResolver generates MutableList when requested`() {
        val resolver = ListResolver(CollectionStrategy.default, Random.Default)

        val result = resolver.resolve(typeOf<MutableList<String>>(), defaultTestChain)
        assertIs<MutableList<*>>(result)
    }

    @Test
    fun `ListResolver canResolve detects List types`() {
        val resolver = ListResolver(CollectionStrategy.default, Random.Default)
        assertTrue(resolver.canResolve(typeOf<List<String>>()))
    }

    @Test
    fun `ListResolver rejects non-List types`() {
        val resolver = ListResolver(CollectionStrategy.default, Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
    }
}
