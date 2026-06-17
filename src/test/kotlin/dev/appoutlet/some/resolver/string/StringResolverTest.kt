package dev.appoutlet.some.resolver.string

import dev.appoutlet.some.config.DefaultStrategyProvider
import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.config.buildSomeConfig
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.Strategy
import dev.appoutlet.some.resolver.nullable.NullableStrategy
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
        val resolver = StringResolver(
            DefaultStrategyProvider(),
            Random.Default
        )

        val result = resolver.resolve(typeOf<String>(), defaultTestChain)
        assertIs<String>(result)
        assertEquals(result.length, StringStrategy.Random().length)
    }

    @Test
    fun `StringResolver generates random string with custom length`() {
        val customLength = 16
        val resolver = StringResolver(
            DefaultStrategyProvider(mapOf(StringStrategy::class to StringStrategy.Random(length = customLength))),
            Random.Default
        )

        val result = resolver.resolve(typeOf<String>(), defaultTestChain)
        assertIs<String>(result)
        assertEquals(result.length, customLength, "Expected length $customLength but got ${result.length}")
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `StringResolver generates UUID when configured`() {
        val resolver = StringResolver(
            DefaultStrategyProvider(mapOf(StringStrategy::class to StringStrategy.Uuid)),
            Random.Default
        )

        val result = resolver.resolve(typeOf<String>(), defaultTestChain) as String
        val uuid = Uuid.parse(result)
        assertIs<String>(result)
        assertEquals(uuid.toString(), result)
    }

    @Test
    fun `StringResolver generates readable strings`() {
        val resolver = StringResolver(
            DefaultStrategyProvider(mapOf(StringStrategy::class to StringStrategy.Readable)),
            Random.Default
        )

        val result = resolver.resolve(typeOf<String>(), defaultTestChain)
        assertIs<String>(result)
        assertTrue(result.startsWith("string-"))
    }

    @Test
    fun `StringResolver canResolve detects String type`() {
        val resolver = StringResolver(DefaultStrategyProvider(), Random.Default)
        assertTrue(resolver.canResolve(typeOf<String>()))
    }

    @Test
    fun `StringResolver rejects non-String types`() {
        val resolver = StringResolver(DefaultStrategyProvider(), Random.Default)
        assertFalse(resolver.canResolve(typeOf<Int>()))
        assertFalse(resolver.canResolve(typeOf<Long>()))
    }

    @Test
    fun `StringStrategy Random with custom length integrates with SomeConfig`() {
        val config = buildSomeConfig {
            strategy(StringStrategy.Random(length = 20))
        }
        val resolvers = config.buildResolvers()
        val chain = ResolverChain(resolvers, config[NullableStrategy::class])

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
