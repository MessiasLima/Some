package dev.appoutlet.some.resolver

import dev.appoutlet.some.test.defaultTestChain
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

enum class TestColor { RED, GREEN, BLUE }

class EnumResolverTest {
    @Test
    fun `EnumResolver picks random enum value`() {
        val resolver = EnumResolver(Random.Default)
        
        val result = resolver.resolve(typeOf<TestColor>(), defaultTestChain)
        assertIs<TestColor>(result)
    }
    
    @Test
    fun `EnumResolver canResolve detects enum types`() {
        val resolver = EnumResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<TestColor>()))
    }
    
    @Test
    fun `EnumResolver rejects non-enum types`() {
        val resolver = EnumResolver(Random.Default)
        assertTrue(!resolver.canResolve(typeOf<String>()))
        assertTrue(!resolver.canResolve(typeOf<Int>()))
    }
}
