package dev.appoutlet.some.resolver

import dev.appoutlet.some.test.defaultTestChain
import java.util.UUID
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi

class JavaUuidResolverTest {
    // UUID format: 8-4-4-4-12 hex digits
    private val uuidPattern = Regex("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")

    @Test
    fun `JavaUuidResolver generates UUID values`() {
        val resolver = JavaUuidResolver()

        val result = resolver.resolve(typeOf<UUID>(), defaultTestChain)
        assertIs<UUID>(result)
    }

    @Test
    fun `JavaUuidResolver generates valid UUID format`() {
        val resolver = JavaUuidResolver()

        repeat(10) {
            val result = resolver.resolve(typeOf<UUID>(), defaultTestChain) as UUID
            val uuidString = result.toString()
            assertTrue(uuidString.matches(uuidPattern), "Expected valid UUID format, got $uuidString")
        }
    }

    @Test
    fun `JavaUuidResolver generates unique UUIDs`() {
        val resolver = JavaUuidResolver()

        val uuids = mutableSetOf<UUID>()
        repeat(100) {
            val result = resolver.resolve(typeOf<UUID>(), defaultTestChain) as UUID
            uuids.add(result)
        }

        // All UUIDs should be unique (very high probability)
        assertEquals(uuids.size, 100, "Expected 100 unique UUIDs, got ${uuids.size}")
    }

    @Test
    fun `JavaUuidResolver canResolve detects UUID type`() {
        val resolver = JavaUuidResolver()
        assertTrue(resolver.canResolve(typeOf<UUID>()))
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `JavaUuidResolver rejects non-UUID types`() {
        val resolver = JavaUuidResolver()
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
        assertFalse(resolver.canResolve(typeOf<Long>()))
        assertFalse(resolver.canResolve(typeOf<kotlin.uuid.Uuid>()))
    }
}
