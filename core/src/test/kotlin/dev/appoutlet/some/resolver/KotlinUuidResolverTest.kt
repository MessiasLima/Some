package dev.appoutlet.some.resolver

import dev.appoutlet.some.test.defaultTestChain
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class KotlinUuidResolverTest {
    // UUID format: 8-4-4-4-12 hex digits
    private val uuidPattern = Regex("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")

    @Test
    fun `KotlinUuidResolver generates Uuid values`() {
        val resolver = KotlinUuidResolver()

        val result = resolver.resolve(typeOf<Uuid>(), defaultTestChain)
        assertIs<Uuid>(result)
    }

    @Test
    fun `KotlinUuidResolver generates valid UUID format`() {
        val resolver = KotlinUuidResolver()

        repeat(10) {
            val result = resolver.resolve(typeOf<Uuid>(), defaultTestChain) as Uuid
            val uuidString = result.toString()
            assertTrue(uuidString.matches(uuidPattern), "Expected valid UUID format, got $uuidString")
        }
    }

    @Test
    fun `KotlinUuidResolver generates unique UUIDs`() {
        val resolver = KotlinUuidResolver()

        val uuids = mutableSetOf<Uuid>()
        repeat(100) {
            val result = resolver.resolve(typeOf<Uuid>(), defaultTestChain) as Uuid
            uuids.add(result)
        }

        // All UUIDs should be unique (very high probability)
        assertEquals(uuids.size, 100, "Expected 100 unique UUIDs, got ${uuids.size}")
    }

    @Test
    fun `KotlinUuidResolver canResolve detects Uuid type`() {
        val resolver = KotlinUuidResolver()
        assertTrue(resolver.canResolve(typeOf<Uuid>()))
    }

    @Test
    fun `KotlinUuidResolver rejects non-Uuid types`() {
        val resolver = KotlinUuidResolver()
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
        assertFalse(resolver.canResolve(typeOf<Long>()))
    }

    @Test
    fun `KotlinUuidResolver rejects Java UUID type`() {
        val resolver = KotlinUuidResolver()
        assertFalse(resolver.canResolve(typeOf<java.util.UUID>()))
    }
}
