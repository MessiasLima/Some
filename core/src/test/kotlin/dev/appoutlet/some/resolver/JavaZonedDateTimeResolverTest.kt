package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.DefaultStrategyProvider
import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.config.ZonedDateTimeStrategy
import dev.appoutlet.some.config.buildSomeConfig
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.test.defaultTestChain
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class JavaZonedDateTimeResolverTest {
    @Test
    fun `JavaZonedDateTimeResolver generates ZonedDateTime values`() {
        val resolver = JavaZonedDateTimeResolver(DefaultStrategyProvider(), Random.Default)

        val result = resolver.resolve(typeOf<ZonedDateTime>(), defaultTestChain)
        assertIs<ZonedDateTime>(result)
    }

    @Test
    fun `JavaZonedDateTimeResolver canResolve detects ZonedDateTime type`() {
        val resolver = JavaZonedDateTimeResolver(DefaultStrategyProvider(), Random.Default)
        assertTrue(resolver.canResolve(typeOf<ZonedDateTime>()))
    }

    @Test
    fun `JavaZonedDateTimeResolver rejects other types`() {
        val resolver = JavaZonedDateTimeResolver(DefaultStrategyProvider(), Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()), "Should not resolve String")
        assertFalse(resolver.canResolve(typeOf<Int>()), "Should not resolve Int")
        assertFalse(resolver.canResolve(typeOf<Instant>()), "Should not resolve java.time.Instant")
    }

    @Test
    fun `Default strategy generates full range values with random zone`() {
        val resolver = JavaZonedDateTimeResolver(DefaultStrategyProvider(), Random.Default)

        repeat(50) {
            val result = resolver.resolve(typeOf<ZonedDateTime>(), defaultTestChain) as ZonedDateTime
            assertTrue(result.toInstant().epochSecond >= Instant.MIN.epochSecond)
            assertTrue(result.toInstant().epochSecond < Instant.MAX.epochSecond)
            assertTrue(result.zone.id in ZoneId.getAvailableZoneIds())
        }
    }

    @Test
    fun `NearPast strategy generates values within ten years before now`() {
        val resolver = JavaZonedDateTimeResolver(
            DefaultStrategyProvider(mapOf(ZonedDateTimeStrategy::class to ZonedDateTimeStrategy.NearPast)),
            Random.Default,
        )

        repeat(100) {
            val before = Instant.now()
            val tenYearsBeforeBefore = before.atZone(ZoneOffset.UTC).minusYears(10).toInstant()
            val result = resolver.resolve(typeOf<ZonedDateTime>(), defaultTestChain) as ZonedDateTime
            val after = Instant.now()

            assertTrue(
                !result.toInstant().isBefore(tenYearsBeforeBefore),
                "Expected result after or at $tenYearsBeforeBefore, got ${result.toInstant()}",
            )
            assertTrue(
                !result.toInstant().isAfter(after),
                "Expected result before or at $after, got ${result.toInstant()}",
            )
        }
    }

    @Test
    fun `NearFuture strategy generates values within ten years after now`() {
        val resolver = JavaZonedDateTimeResolver(
            DefaultStrategyProvider(mapOf(ZonedDateTimeStrategy::class to ZonedDateTimeStrategy.NearFuture)),
            Random.Default,
        )

        repeat(100) {
            val before = Instant.now()
            val result = resolver.resolve(typeOf<ZonedDateTime>(), defaultTestChain) as ZonedDateTime
            val after = Instant.now()
            val tenYearsAfterAfter = after.atZone(ZoneOffset.UTC).plusYears(10).toInstant()

            assertTrue(
                !result.toInstant().isBefore(before),
                "Expected result after or at $before, got ${result.toInstant()}",
            )
            assertTrue(
                !result.toInstant().isAfter(tenYearsAfterAfter),
                "Expected result before or at $tenYearsAfterAfter, got ${result.toInstant()}",
            )
        }
    }

    @Test
    fun `DistantPast strategy generates values from Instant MIN until now`() {
        val resolver = JavaZonedDateTimeResolver(
            DefaultStrategyProvider(mapOf(ZonedDateTimeStrategy::class to ZonedDateTimeStrategy.DistantPast)),
            Random.Default,
        )

        repeat(100) {
            val result = resolver.resolve(typeOf<ZonedDateTime>(), defaultTestChain) as ZonedDateTime
            assertTrue(!result.toInstant().isBefore(Instant.MIN))
            assertTrue(!result.toInstant().isAfter(Instant.now()))
        }
    }

    @Test
    fun `DistantFuture strategy generates values from now until Instant MAX`() {
        val resolver = JavaZonedDateTimeResolver(
            DefaultStrategyProvider(mapOf(ZonedDateTimeStrategy::class to ZonedDateTimeStrategy.DistantFuture)),
            Random.Default,
        )

        repeat(100) {
            val before = Instant.now()
            val result = resolver.resolve(typeOf<ZonedDateTime>(), defaultTestChain) as ZonedDateTime
            assertTrue(!result.toInstant().isBefore(before))
            assertTrue(!result.toInstant().isAfter(Instant.MAX))
        }
    }

    @Test
    fun `Range strategy generates values within inclusive bounds and random zone`() {
        val min = Instant.parse("2020-01-01T00:00:00Z")
        val max = Instant.parse("2025-01-01T00:00:00Z")
        val resolver = JavaZonedDateTimeResolver(
            DefaultStrategyProvider(mapOf(ZonedDateTimeStrategy::class to ZonedDateTimeStrategy.Range(min, max))),
            Random.Default,
        )

        repeat(100) {
            val result = resolver.resolve(typeOf<ZonedDateTime>(), defaultTestChain) as ZonedDateTime
            assertTrue(result.zone.id in ZoneId.getAvailableZoneIds())
            assertTrue(
                !result.toInstant().isBefore(min),
                "Expected result after or at $min, got ${result.toInstant()}",
            )
            assertTrue(
                !result.toInstant().isAfter(max),
                "Expected result before or at $max, got ${result.toInstant()}",
            )
        }
    }

    @Test
    fun `Range strategy with zoneId uses supplied zone`() {
        val min = Instant.parse("2020-01-01T00:00:00Z")
        val max = Instant.parse("2025-01-01T00:00:00Z")
        val zoneId = ZoneId.of("America/Sao_Paulo")
        val resolver = JavaZonedDateTimeResolver(
            DefaultStrategyProvider(
                mapOf(ZonedDateTimeStrategy::class to ZonedDateTimeStrategy.Range(min, max, zoneId))
            ),
            Random.Default,
        )

        repeat(50) {
            val result = resolver.resolve(typeOf<ZonedDateTime>(), defaultTestChain) as ZonedDateTime
            assertEquals(zoneId, result.zone)
            assertTrue(!result.toInstant().isBefore(min))
            assertTrue(!result.toInstant().isAfter(max))
        }
    }

    @Test
    fun `ZonedDateTimeStrategy integrates with SomeConfig`() {
        val min = Instant.parse("2020-01-01T00:00:00Z")
        val max = Instant.parse("2025-01-01T00:00:00Z")
        val zoneId = ZoneId.of("Europe/London")
        val config = buildSomeConfig {
            strategy(ZonedDateTimeStrategy.Range(min, max, zoneId))
        }
        val resolvers = config.buildResolvers()
        val chain = ResolverChain(resolvers, config[NullableStrategy::class])

        repeat(50) {
            val result = chain.resolve(typeOf<ZonedDateTime>()) as ZonedDateTime
            assertEquals(zoneId, result.zone)
            assertTrue(!result.toInstant().isBefore(min))
            assertTrue(!result.toInstant().isAfter(max))
        }
    }
}
