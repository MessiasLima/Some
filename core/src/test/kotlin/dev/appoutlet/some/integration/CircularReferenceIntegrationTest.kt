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
            strategy(NullableStrategy.NullOnCircularReference)
        }

        val result = assertIs<Node>(node)
        assertNull(result.next)
    }

    @Test
    fun `nullable top-level circular type throws under NeverNull strategy`() {
        assertFailsWith<SomeCircularReferenceException> {
            some<Node?> {
                strategy(NullableStrategy.NeverNull)
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
                strategy(NullableStrategy.NeverNull)
            }
        }
    }

    @Test
    fun `AlwaysNull strategy handles circular reference by returning null`() {
        // In this case NullableResolver will return null even before ResolverChain detects a cycle
        val node: Node = some<Node> {
            strategy(NullableStrategy.AlwaysNull)
        }

        assertNull(node.next)
    }

    @Test
    fun `Random strategy still throws on circular reference when it decides to resolve non-null`() {
        // With probability 0, it's like NeverNull
        assertFailsWith<SomeCircularReferenceException> {
            some<Node> {
                strategy(NullableStrategy.Random(probability = 0.0))
            }
        }
    }

    data class Companies(val companies: List<Company>)
    data class Company(val name: String, val employees: List<Employee>)
    data class Employee(val name: String)

    @Test
    fun `false circular reference`() {
        some<Companies>()
    }

    data class Directories(val companies: Map<String, DirectoryCompany>)
    data class DirectoryCompany(val name: String, val employees: Map<String, DirectoryEmployee>)
    data class DirectoryEmployee(val name: String)

    @Test
    fun `different generic map value types do not trigger circular reference detection`() {
        some<Directories>()
    }

    data class RecursiveNode(val children: List<RecursiveNode>)

    @Test
    fun `repeated generic type is detected as a circular reference`() {
        assertFailsWith<SomeCircularReferenceException> {
            some<RecursiveNode>()
        }
    }

    data class Teams(val teams: List<Team>)
    data class Team(val name: String, val members: List<Member>)
    data class Member(val name: String)

    @Test
    fun `different generic collection element types do not trigger circular reference detection`() {
        some<Teams>()
    }
}
