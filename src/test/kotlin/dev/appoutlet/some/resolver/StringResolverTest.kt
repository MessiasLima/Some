package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.config.StringStrategy
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.test.defaultTestChain
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class StringResolverTest {
    @Test
    fun `StringResolver respects the default lenght`() {
        val strategy = StringStrategy.Random()
        val resolver = StringResolver(strategy, Random.Default)
        
        val result = resolver.resolve(typeOf<String>(), defaultTestChain)
        assertIs<String>(result)
        assertEquals(result.length, strategy.length)
    }
    
    @Test
    fun `StringResolver generates random string with custom length`() {
        val customLength = 16
        val resolver = StringResolver(StringStrategy.Random(length = customLength), Random.Default)
        
        val result = resolver.resolve(typeOf<String>(), defaultTestChain)
        assertIs<String>(result)
        assertEquals(result.length, customLength, "Expected length $customLength but got ${result.length}")
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `StringResolver generates UUID when configured`() {
        val resolver = StringResolver(StringStrategy.Uuid, Random.Default)
        
        val result = resolver.resolve(typeOf<String>(), defaultTestChain) as String
        val uuid = Uuid.parse(result)
        assertIs<String>(result)
        assertEquals(uuid.toString(), result)
    }
    
    @Test
    fun `StringResolver generates readable strings`() {
        val resolver = StringResolver(StringStrategy.Readable, Random.Default)
        
        val result = resolver.resolve(typeOf<String>(), defaultTestChain)
        assertIs<String>(result)
        assertTrue(result.startsWith("string-"))
    }
    
    @Test
    fun `StringResolver canResolve detects String type`() {
        val resolver = StringResolver(StringStrategy.Random(), Random.Default)
        assertTrue(resolver.canResolve(typeOf<String>()))
    }
    
    @Test
    fun `StringResolver rejects non-String types`() {
        val resolver = StringResolver(StringStrategy.Random(), Random.Default)
        assertFalse(resolver.canResolve(typeOf<Int>()))
        assertFalse(resolver.canResolve(typeOf<Long>()))
    }
    
    @Test
    fun `StringStrategy Random with custom length integrates with SomeConfig`() {
        val config = SomeConfig().apply {
            stringStrategy = StringStrategy.Random(length = 20)
        }
        val resolvers = config.buildResolvers()
        val chain = ResolverChain(resolvers)
        
        val result = chain.resolve(typeOf<String>())
        assertIs<String>(result)
        assertEquals(result.length, 20)
    }
    
    @Test
    fun `StringStrategy Random rejects length less than or equal to 1`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            StringStrategy.Random(length = 0)
        }
        assertEquals("Length must be greater than 0", exception.message)
    }

    @Test
    fun `StringStrategy Random rejects negative length`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            StringStrategy.Random(length = -5)
        }
        assertEquals("Length must be greater than 0", exception.message)
    }
}
