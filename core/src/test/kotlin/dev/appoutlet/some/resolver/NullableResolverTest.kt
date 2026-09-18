package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.DefaultStrategyProvider
import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.config.buildSomeConfig
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.exception.SomeCircularReferenceException
import dev.appoutlet.some.test.defaultTestChain
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NullableResolverTest {
    private data class Node(val next: Node?)

    @Test
    fun `NullableResolver with AlwaysNull strategy returns null`() {
        val resolver = NullableResolver(
            DefaultStrategyProvider(mapOf(NullableStrategy::class to NullableStrategy.AlwaysNull)),
            Random.Default
        )

        repeat(1000) {
            val result = resolver.resolve(typeOf<String?>(), defaultTestChain)
            assertNull(result)
        }
    }

    @Test
    fun `NullableResolver with NeverNull strategy generates non-null values`() {
        val config = buildSomeConfig {
            strategy(NullableStrategy.NeverNull)
        }
        val resolvers = config.buildResolvers()

        repeat(1000) {
            val result = ResolverChain(resolvers).resolve(typeOf<String?>())
            assertNotNull(result)
            assertIs<String>(result)
        }
    }

    @Test
    fun `NullableResolver with Random strategy can return null or value`() {
        val config = buildSomeConfig {
            strategy(NullableStrategy.Random())
        }
        val resolvers = config.buildResolvers()

        val results = (1..100).map {
            ResolverChain(resolvers).resolve(typeOf<String?>())
        }
        val hasNull = results.any { it == null }
        val hasValue = results.any { it != null }
        assertTrue(hasNull && hasValue)
    }

    @Test
    fun `NullableResolver with Random strategy and probability 0_0 always returns non-null`() {
        val config = buildSomeConfig {
            strategy(NullableStrategy.Random(probability = 0.0))
        }
        val resolvers = config.buildResolvers()

        repeat(1000) {
            val result = ResolverChain(resolvers).resolve(typeOf<String?>())
            assertNotNull(result)
            assertIs<String>(result)
        }
    }

    @Test
    fun `NullableResolver with Random strategy and probability 1_0 always returns null`() {
        val config = buildSomeConfig {
            strategy(NullableStrategy.Random(probability = 1.0))
        }
        val resolvers = config.buildResolvers()

        repeat(1000) {
            val result = ResolverChain(resolvers).resolve(typeOf<String?>())
            assertNull(result)
        }
    }

    @Test
    fun `NullableResolver canResolve detects nullable types`() {
        val resolver = NullableResolver(DefaultStrategyProvider(), Random.Default)
        assertTrue(resolver.canResolve(typeOf<String?>()))
    }

    @Test
    fun `NullableResolver rejects non-nullable types`() {
        val resolver = NullableResolver(DefaultStrategyProvider(), Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
    }

    @Test
    fun `direct ResolverChain resolution always throws on detected circular reference`() {
        class RecursiveResolver : Resolver {
            override fun canResolve(type: KType): Boolean = type == typeOf<String>()
            override fun resolve(type: KType, chain: ResolverChain): Any? {
                return chain.resolve(typeOf<String>())
            }
        }

        val chain = ResolverChain(listOf(RecursiveResolver()))
        assertFailsWith<SomeCircularReferenceException> {
            chain.resolve(typeOf<String>())
        }
    }

    @Test
    fun `NullableResolver converts SomeCircularReferenceException to null for NullOnCircularReference`() {
        val config = buildSomeConfig {
            strategy(NullableStrategy.NullOnCircularReference)
        }
        val resolvers = config.buildResolvers()
        val chain = ResolverChain(resolvers)

        val node = chain.resolve(typeOf<Node>()) as Node
        assertNull(node.next)
    }

    @Test
    fun `NullableResolver propagates SomeCircularReferenceException for NeverNull strategy`() {
        val config = buildSomeConfig {
            strategy(NullableStrategy.NeverNull)
        }
        val resolvers = config.buildResolvers()
        val chain = ResolverChain(resolvers)

        assertFailsWith<SomeCircularReferenceException> {
            chain.resolve(typeOf<Node>())
        }
    }

    @Test
    fun `NullableResolver propagates SomeCircularReferenceException for Random strategy with probability 0_0`() {
        val config = buildSomeConfig {
            strategy(NullableStrategy.Random(probability = 0.0))
        }
        val resolvers = config.buildResolvers()
        val chain = ResolverChain(resolvers)

        assertFailsWith<SomeCircularReferenceException> {
            chain.resolve(typeOf<Node>())
        }
    }
}
