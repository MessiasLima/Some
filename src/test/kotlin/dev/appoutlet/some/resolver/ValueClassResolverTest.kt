package dev.appoutlet.some.resolver

import dev.appoutlet.some.test.defaultTestChain
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

// Test value classes
@JvmInline
value class UserId(val id: Int)

@JvmInline
value class Email(val value: String)

@JvmInline
value class Score(val points: Long)

class ValueClassResolverTest {
    @Test
    fun `ValueClassResolver generates value class instances`() {
        val resolver = ValueClassResolver()

        val result = resolver.resolve(typeOf<UserId>(), defaultTestChain)
        assertIs<UserId>(result)
    }

    @Test
    fun `ValueClassResolver resolves underlying Int value correctly`() {
        val resolver = ValueClassResolver()

        val result = resolver.resolve(typeOf<UserId>(), defaultTestChain) as UserId
        assertIs<Int>(result.id)
    }

    @Test
    fun `ValueClassResolver resolves underlying String value correctly`() {
        val resolver = ValueClassResolver()

        val result = resolver.resolve(typeOf<Email>(), defaultTestChain) as Email
        assertIs<String>(result.value)
        assertTrue(result.value.isNotEmpty())
    }

    @Test
    fun `ValueClassResolver resolves underlying Long value correctly`() {
        val resolver = ValueClassResolver()

        val result = resolver.resolve(typeOf<Score>(), defaultTestChain) as Score
        assertIs<Long>(result.points)
    }

    @Test
    fun `ValueClassResolver canResolve detects value classes`() {
        val resolver = ValueClassResolver()
        assertTrue(resolver.canResolve(typeOf<UserId>()))
        assertTrue(resolver.canResolve(typeOf<Email>()))
        assertTrue(resolver.canResolve(typeOf<Score>()))
    }

    @Test
    fun `ValueClassResolver rejects non-value classes`() {
        val resolver = ValueClassResolver()
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
        assertFalse(resolver.canResolve(typeOf<Long>()))
    }

    @Test
    fun `ValueClassResolver handles multiple value classes with same underlying type`() {
        val resolver = ValueClassResolver()

        // Both use Int, but are different types
        val userId = resolver.resolve(typeOf<UserId>(), defaultTestChain) as UserId
        assertIs<UserId>(userId)

        // Score uses Long
        val score = resolver.resolve(typeOf<Score>(), defaultTestChain) as Score
        assertIs<Score>(score)
    }
}
