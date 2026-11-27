package dev.appoutlet.some.core

import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.exception.SomeUnresolvableTypeException
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ResolverChainTest {
    data class Node(val next: Node?)

    @Test
    fun `resolve reuses cached resolver for repeated type`() {
        val resolver = RecordingResolver(value = { "resolved" })
        val chain = ResolverChain(listOf(resolver), null)

        assertEquals("resolved", chain.resolve(typeOf<String>()))
        assertEquals("resolved", chain.resolve(typeOf<String>()))

        assertEquals(listOf(typeOf<String>()), resolver.checkedTypes)
        assertEquals(2, resolver.resolveCount)
    }

    @Test
    fun `resolve preserves first-match-wins ordering when cached`() {
        val first = RecordingResolver(supported = { it == typeOf<Int>() }, value = { "first" })
        val second = RecordingResolver(value = { "second" })
        val chain = ResolverChain(listOf(first, second), null)

        assertEquals("first", chain.resolve(typeOf<Int>()))
        assertEquals("first", chain.resolve(typeOf<Int>()))

        assertEquals(listOf(typeOf<Int>()), first.checkedTypes)
        assertTrue(second.checkedTypes.isEmpty())
    }

    @Test
    fun `resolve caches distinct generic and nullable types independently`() {
        val resolver = RecordingResolver(value = { it.toString() })
        val chain = ResolverChain(listOf(resolver), null)
        val types = listOf(
            typeOf<List<String>>(),
            typeOf<List<Int>>(),
            typeOf<List<String>?>(),
            typeOf<Set<String>>(),
        )

        types.forEach { assertEquals(it.toString(), chain.resolve(it)) }
        types.forEach { chain.resolve(it) }

        assertEquals(types, resolver.checkedTypes)
    }

    @Test
    fun `resolve caches unsupported type failures`() {
        val resolver = RecordingResolver(supported = { false })
        val chain = ResolverChain(listOf(resolver), null)

        val first = assertFailsWith<SomeUnresolvableTypeException> { chain.resolve(typeOf<String>()) }
        val second = assertFailsWith<SomeUnresolvableTypeException> { chain.resolve(typeOf<String>()) }

        assertEquals(first.message, second.message)
        assertEquals(listOf(typeOf<String>()), resolver.checkedTypes)
    }

    @Test
    fun `resolve reuses cached lookups for repeated nested collection elements`() {
        val stringResolver = RecordingResolver(supported = { it == typeOf<String>() })
        val chain = ResolverChain(listOf(stringResolver, StringListResolver(size = 4)), null)

        val values = assertIs<List<*>>(chain.resolve(typeOf<List<String>>()))

        assertEquals(4, values.size)
        assertEquals(1, stringResolver.checkedTypes.count { it == typeOf<String>() })
    }

    @Test
    fun `resolve keeps circular reference behavior and stack cleanup when cache is hit`() {
        val config = SomeConfig()
        val checks = mutableListOf<KType>()
        val chain = ResolverChain(
            config.buildResolvers().map { CountingResolver(it, checks) },
            config[NullableStrategy::class],
        )

        val first = assertIs<Node>(chain.resolve(typeOf<Node>()))
        val checksAfterFirst = checks.toList()
        val second = assertIs<Node>(chain.resolve(typeOf<Node>()))

        assertNull(first.next)
        assertNull(second.next)
        assertEquals(checksAfterFirst, checks)
        assertTrue(chain.stack.isEmpty())
    }

    @Test
    fun `resolve keeps caches isolated between chains`() {
        val first = RecordingResolver()
        val second = RecordingResolver()
        val firstChain = ResolverChain(listOf(first), null)
        val secondChain = ResolverChain(listOf(second), null)

        firstChain.resolve(typeOf<String>())
        firstChain.resolve(typeOf<String>())
        secondChain.resolve(typeOf<String>())
        secondChain.resolve(typeOf<String>())

        assertEquals(1, first.checkedTypes.size)
        assertEquals(1, second.checkedTypes.size)
    }

    @Test
    fun `resolve removes type from stack when resolver fails`() {
        val resolver = object : Resolver {
            override fun canResolve(type: KType): Boolean = true

            override fun resolve(type: KType, chain: ResolverChain): Any? = error("Resolution failed")
        }
        val chain = ResolverChain(listOf(resolver), null)

        assertFailsWith<IllegalStateException> { chain.resolve(typeOf<String>()) }
        assertTrue(chain.stack.isEmpty())
    }
}

private class RecordingResolver(
    private val supported: (KType) -> Boolean = { true },
    private val value: (KType) -> Any? = { "resolved" },
) : Resolver {
    val checkedTypes = mutableListOf<KType>()
    var resolveCount = 0
        private set

    override fun canResolve(type: KType): Boolean {
        checkedTypes += type
        return supported(type)
    }

    override fun resolve(type: KType, chain: ResolverChain): Any? {
        resolveCount++
        return value(type)
    }
}

private class StringListResolver(
    private val size: Int,
) : Resolver {
    override fun canResolve(type: KType): Boolean = type == typeOf<List<String>>()

    override fun resolve(type: KType, chain: ResolverChain): Any = List(size) { chain.resolve(typeOf<String>()) }
}

private class CountingResolver(
    private val delegate: Resolver,
    private val checks: MutableList<KType>,
) : Resolver {
    override fun canResolve(type: KType): Boolean {
        checks += type
        return delegate.canResolve(type)
    }

    override fun resolve(type: KType, chain: ResolverChain): Any? = delegate.resolve(type, chain)
}
