package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.SomeConfig
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class LongResolverTest {
    private val config = SomeConfig()
    private val chain = config.buildChain()
    
    @Test
    fun `LongResolver generates long values`() {
        val resolver = LongResolver(Random.Default)
        
        val result = resolver.resolve(typeOf<Long>(), chain)
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
        assertTrue(!resolver.canResolve(typeOf<String>()))
        assertTrue(!resolver.canResolve(typeOf<Int>()))
    }
}
