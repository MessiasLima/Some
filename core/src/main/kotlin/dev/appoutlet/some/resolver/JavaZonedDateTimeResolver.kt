package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.ZonedDateTimeStrategy
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.StrategyProvider
import dev.appoutlet.some.core.get
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

private const val TEN_YEARS = 10L

/**
 * Resolves [ZonedDateTime] instances.
 *
 * Generated values are controlled by the active [ZonedDateTimeStrategy]. By default, values are
 * spread across the full [Instant] range and use a randomly selected [ZoneId] from the available
 * set on the JVM.
 *
 * @param strategyProvider Provider of configured strategies.
 * @param random The random source used for generation.
 */
class JavaZonedDateTimeResolver(
    private val strategyProvider: StrategyProvider,
    private val random: Random,
) : Resolver {
    private val zoneIds by lazy { ZoneId.getAvailableZoneIds().toList() }

    override fun canResolve(type: KType): Boolean {
        return type == typeOf<ZonedDateTime>()
    }

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val strategy = strategyProvider.get<ZonedDateTimeStrategy>() ?: ZonedDateTimeStrategy.default
        val now = Instant.now()

        val (minInstant, maxInstant) = when (strategy) {
            ZonedDateTimeStrategy.Default -> Instant.MIN to Instant.MAX
            ZonedDateTimeStrategy.NearPast -> now.minusYears(TEN_YEARS) to now
            ZonedDateTimeStrategy.NearFuture -> now to now.plusYears(TEN_YEARS)
            ZonedDateTimeStrategy.DistantPast -> Instant.MIN to now
            ZonedDateTimeStrategy.DistantFuture -> now to Instant.MAX
            is ZonedDateTimeStrategy.Range -> strategy.min to strategy.max
        }

        val epochSecond = randomEpochSecond(minInstant, maxInstant)

        val zoneId = when (strategy) {
            is ZonedDateTimeStrategy.Range -> strategy.zoneId ?: ZoneId.of(zoneIds.random())
            else -> ZoneId.of(zoneIds.random())
        }

        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), zoneId)
    }

    private fun randomEpochSecond(min: Instant, max: Instant): Long {
        return random.nextLong(min.epochSecond, max.epochSecond)
    }

    private fun Instant.minusYears(years: Long): Instant =
        this.atZone(ZoneOffset.UTC).minusYears(years).toInstant()

    private fun Instant.plusYears(years: Long): Instant =
        this.atZone(ZoneOffset.UTC).plusYears(years).toInstant()
}
