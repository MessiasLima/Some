package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.SomeConfig
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.test.assertTrue

object TestSingletonObject {
    val value = 42
}

class ObjectResolverTest {
    private val config = SomeConfig()
    private val chain = config.buildChain()
    
    @Test
    fun `ObjectResolver returns singleton`() {
        val resolver = ObjectResolver()
        
        val result = resolver.resolve(typeOf<TestSingletonObject>(), chain)
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
        assertTrue(!resolver.canResolve(typeOf<String>()))
        assertTrue(!resolver.canResolve(typeOf<Int>()))
    }
}
