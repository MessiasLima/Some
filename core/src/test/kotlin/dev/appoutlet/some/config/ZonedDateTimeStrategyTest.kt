package dev.appoutlet.some.config

import java.time.Instant
import java.time.ZoneId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class ZonedDateTimeStrategyTest {
    @Test
    fun `default strategy is Default`() {
        assertSame(ZonedDateTimeStrategy.Default, ZonedDateTimeStrategy.default)
    }

    @Test
    fun `Range accepts valid min and max`() {
        val min = Instant.parse("2020-01-01T00:00:00Z")
        val max = Instant.parse("2025-01-01T00:00:00Z")

        val strategy = ZonedDateTimeStrategy.Range(min, max)

        assertEquals(min, strategy.min)
        assertEquals(max, strategy.max)
        assertEquals(null, strategy.zoneId)
    }

    @Test
    fun `Range accepts equal min and max`() {
        val instant = Instant.parse("2020-01-01T00:00:00Z")

        val strategy = ZonedDateTimeStrategy.Range(instant, instant)

        assertEquals(instant, strategy.min)
        assertEquals(instant, strategy.max)
    }

    @Test
    fun `Range accepts custom zoneId`() {
        val min = Instant.parse("2020-01-01T00:00:00Z")
        val max = Instant.parse("2025-01-01T00:00:00Z")
        val zoneId = ZoneId.of("America/Sao_Paulo")

        val strategy = ZonedDateTimeStrategy.Range(min, max, zoneId)

        assertEquals(zoneId, strategy.zoneId)
    }

    @Test
    fun `Range rejects inverted min and max`() {
        val min = Instant.parse("2025-01-01T00:00:00Z")
        val max = Instant.parse("2020-01-01T00:00:00Z")

        val error = assertFailsWith<IllegalArgumentException> {
            ZonedDateTimeStrategy.Range(min, max)
        }

        assertEquals(
            "ZonedDateTimeStrategy.Range requires min <= max, but got min=$min and max=$max",
            error.message,
        )
    }

    @Test
    fun `key is ZonedDateTimeStrategy class`() {
        assertSame(ZonedDateTimeStrategy::class, ZonedDateTimeStrategy.Default.key)
        assertSame(ZonedDateTimeStrategy::class, ZonedDateTimeStrategy.NearPast.key)
        assertSame(
            ZonedDateTimeStrategy::class,
            ZonedDateTimeStrategy.Range(Instant.MIN, Instant.MAX).key,
        )
    }
}
