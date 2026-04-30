package dev.appoutlet.some.resolver

import dev.appoutlet.some.test.defaultTestChain
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ByteResolverTest {
    @Test
    fun `ByteResolver generates byte values`() {
        val resolver = ByteResolver(Random.Default)

        val result = resolver.resolve(typeOf<Byte>(), defaultTestChain)
        assertIs<Byte>(result)
    }

    @Test
    fun `ByteResolver canResolve detects Byte type`() {
        val resolver = ByteResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<Byte>()))
    }

    @Test
    fun `ByteResolver rejects non-Byte types`() {
        val resolver = ByteResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
        assertFalse(resolver.canResolve(typeOf<Short>()))
    }
}
