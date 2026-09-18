package dev.appoutlet.some.compat.kotlinfixture

import dev.appoutlet.some.exception.SomeCircularReferenceException
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class RecursionStrategyCompatibilityTest {

    @Test
    fun `NullRecursionStrategy resolves circular reference as null for nullable field`() {
        val fixture = kotlinFixture {
            recursionStrategy = RecursionStrategy.NullRecursionStrategy
        }

        val node: RecursiveNode = fixture()
        assertNull(node.next)
    }

    @Test
    fun `ThrowingRecursionStrategy throws exception on circular reference`() {
        val fixture = kotlinFixture {
            recursionStrategy = RecursionStrategy.ThrowingRecursionStrategy
        }

        assertFailsWith<SomeCircularReferenceException> {
            fixture<RecursiveNode>()
        }
    }

    @Test
    fun `UnresolvedRecursionStrategy throws exception on circular reference`() {
        val fixture = kotlinFixture {
            recursionStrategy = RecursionStrategy.UnresolvedRecursionStrategy
        }

        assertFailsWith<SomeCircularReferenceException> {
            fixture<RecursiveNode>()
        }
    }
}
