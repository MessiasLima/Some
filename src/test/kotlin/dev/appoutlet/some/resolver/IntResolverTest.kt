package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.core.FixtureContext
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class IntResolverTest {
    private val config = SomeConfig()
    private val chain = config.buildChain()
    
    @Test
    fun `IntResolver generates int values`() {
        val resolver = IntResolver()
        val context = FixtureContext(Random.Default, emptyList())
        
        val result = resolver.resolve(typeOf<Int>(), context, chain)
        assertIs<Int>(result)
    }
    
    @Test
    fun `IntResolver canResolve detects Int type`() {
        val resolver = IntResolver()
        assertTrue(resolver.canResolve(typeOf<Int>()))
    }
    
    @Test
    fun `IntResolver rejects non-Int types`() {
        val resolver = IntResolver()
        assertTrue(!resolver.canResolve(typeOf<String>()))
        assertTrue(!resolver.canResolve(typeOf<Long>()))
    }
}
