package dev.appoutlet.some.integration

import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.exception.SomeCircularReferenceException
import dev.appoutlet.some.some
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull

class CircularReferenceIntegrationTest {
    data class Node(val next: Node?)
    data class StrictNode(val next: StrictNode)
    data class IndirectA(val b: IndirectB?)
    data class IndirectB(val a: IndirectA?)

    @Test
    fun `circular nullable field returns null under NullOnCircularReference by default`() {
        val node: Node = some<Node>()
        assertNull(node.next)
    }

    @Test
    fun `nullable top-level circular type returns instance with null circular field by default`() {
        val node: Node? = some<Node?>()

        val result = assertIs<Node>(node)
        assertNull(result.next)
    }

    @Test
    fun `nullable top-level circular type returns instance with null circular field under NullOnCircularReference`() {
        val node: Node? = some<Node?> {
            nullableStrategy = NullableStrategy.NullOnCircularReference
        }

        val result = assertIs<Node>(node)
        assertNull(result.next)
    }

    @Test
    fun `nullable top-level circular type throws under NeverNull strategy`() {
        assertFailsWith<SomeCircularReferenceException> {
            some<Node?> {
                nullableStrategy = NullableStrategy.NeverNull
            }
        }
    }

    @Test
    fun `nullable top-level indirect circular type returns instance with null circular field by default`() {
        val a: IndirectA? = some<IndirectA?>()

        val result = assertIs<IndirectA>(a)
        val b = assertIs<IndirectB>(result.b)
        assertNull(b.a)
    }

    @Test
    fun `nullable top-level strict circular type still throws by default`() {
        assertFailsWith<SomeCircularReferenceException> {
            some<StrictNode?>()
        }
    }

    @Test
    fun `circular non-nullable field still throws SomeCircularReferenceException`() {
        assertFailsWith<SomeCircularReferenceException> {
            some<StrictNode>()
        }
    }

    @Test
    fun `indirect circular reference returns null for nullable field`() {
        val a: IndirectA = some<IndirectA>()
        assertIs<IndirectB>(a.b)
        assertNull(a.b.a)
    }

    @Test
    fun `NeverNull strategy still throws on circular reference even if nullable`() {
        assertFailsWith<SomeCircularReferenceException> {
            some<Node> {
                nullableStrategy = NullableStrategy.NeverNull
            }
        }
    }

    @Test
    fun `AlwaysNull strategy handles circular reference by returning null`() {
        // In this case NullableResolver will return null even before ResolverChain detects a cycle
        val node: Node = some<Node> {
            nullableStrategy = NullableStrategy.AlwaysNull
        }

        assertNull(node.next)
    }

    @Test
    fun `Random strategy still throws on circular reference when it decides to resolve non-null`() {
        // With probability 0, it's like NeverNull
        assertFailsWith<SomeCircularReferenceException> {
            some<Node> {
                nullableStrategy = NullableStrategy.Random(probability = 0.0)
            }
        }
    }
}
