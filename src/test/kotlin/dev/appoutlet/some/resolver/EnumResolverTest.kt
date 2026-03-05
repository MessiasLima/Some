package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.core.FixtureContext
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

enum class TestColor { RED, GREEN, BLUE }

class EnumResolverTest {
    private val config = SomeConfig()
    private val chain = config.buildChain()
    
    @Test
    fun `EnumResolver picks random enum value`() {
        val resolver = EnumResolver()
        val context = FixtureContext(Random.Default, emptyList(), SomeConfig())
        
        val result = resolver.resolve(typeOf<TestColor>(), context, chain)
        assertIs<TestColor>(result)
    }
    
    @Test
    fun `EnumResolver canResolve detects enum types`() {
        val resolver = EnumResolver()
        assertTrue(resolver.canResolve(typeOf<TestColor>()))
    }
    
    @Test
    fun `EnumResolver rejects non-enum types`() {
        val resolver = EnumResolver()
        assertTrue(!resolver.canResolve(typeOf<String>()))
        assertTrue(!resolver.canResolve(typeOf<Int>()))
    }
}
