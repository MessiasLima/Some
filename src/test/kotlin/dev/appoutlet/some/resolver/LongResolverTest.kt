package dev.appoutlet.some.resolver

import dev.appoutlet.some.test.defaultTestChain
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class LongResolverTest {
    @Test
    fun `LongResolver generates long values`() {
        val resolver = LongResolver(Random.Default)

        val result = resolver.resolve(typeOf<Long>(), defaultTestChain)
        assertIs<Long>(result)
    }

    @Test
    fun `LongResolver canResolve detects Long type`() {
        val resolver = LongResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<Long>()))
    }

    @Test
    fun `LongResolver rejects non-Long types`() {
        val resolver = LongResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
    }
}
