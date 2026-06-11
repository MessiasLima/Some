package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.CollectionStrategy
import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.test.defaultTestChain
import org.junit.jupiter.api.Assertions.assertFalse
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ArrayResolverTest {
    @Test
    fun `ArrayResolver generates array with correct size`() {
        val config = SomeConfig().toBuilder().apply {
            collectionStrategy = CollectionStrategy(3..5)
        }.build()
        val resolvers = config.buildResolvers()
        val resolver = ArrayResolver(config, Random.Default)

        val result = resolver.resolve(typeOf<Array<String>>(), ResolverChain(resolvers, config))
        assertIs<Array<*>>(result)
        assertTrue(result.size in 3..5)
        assertTrue(result.all { it is String })
    }

    @Test
    fun `ArrayResolver generates array with Int elements`() {
        val resolver = ArrayResolver(SomeConfig(), Random.Default)

        val result = resolver.resolve(typeOf<Array<Int>>(), defaultTestChain)
        assertIs<Array<*>>(result)
        assertTrue(result.all { it is Int })
    }

    @Test
    fun `ArrayResolver generates array with Class elements`() {
        data class User(val name: String, val age: Int)

        val resolver = ArrayResolver(SomeConfig(), Random.Default)

        val result = resolver.resolve(typeOf<Array<User>>(), defaultTestChain)
        assertIs<Array<*>>(result)
        assertTrue(result.all { it is User })
    }

    @Test
    fun `ArrayResolver canResolve detects Array types`() {
        val resolver = ArrayResolver(SomeConfig(), Random.Default)
        assertTrue(resolver.canResolve(typeOf<Array<String>>()))
        assertTrue(resolver.canResolve(typeOf<Array<Int>>()))
    }

    @Test
    fun `ArrayResolver rejects non-Array types`() {
        val resolver = ArrayResolver(SomeConfig(), Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
        assertFalse(resolver.canResolve(typeOf<List<String>>()))
    }

    @Test
    fun `ArrayResolver throws error on star projection`() {
        val resolver = ArrayResolver(SomeConfig(), Random.Default)

        assertFailsWith<IllegalStateException> {
            resolver.resolve(typeOf<Array<*>>(), defaultTestChain)
        }
    }
}
