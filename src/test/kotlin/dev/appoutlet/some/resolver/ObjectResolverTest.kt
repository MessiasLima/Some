package dev.appoutlet.some.resolver

import dev.appoutlet.some.test.defaultTestChain
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

object TestSingletonObject {
    val value = 42
}

class ObjectResolverTest {
    @Test
    fun `ObjectResolver returns singleton`() {
        val resolver = ObjectResolver()

        val result = resolver.resolve(typeOf<TestSingletonObject>(), defaultTestChain)
        assertSame(result, TestSingletonObject)
    }

    @Test
    fun `ObjectResolver canResolve detects object types`() {
        val resolver = ObjectResolver()
        assertTrue(resolver.canResolve(typeOf<TestSingletonObject>()))
    }

    @Test
    fun `ObjectResolver rejects non-object types`() {
        val resolver = ObjectResolver()
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
    }
}
