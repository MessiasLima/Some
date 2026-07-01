package dev.appoutlet.some.config

import java.time.Instant
import java.time.ZoneId

/**
 * Strategy for controlling how [java.time.ZonedDateTime] values are generated.
 *
 * The strategy selects the time window for generated instants and, for [Range],
 * an optional fixed [ZoneId]. All non-[Range] variants use a random available JVM [ZoneId].
 *
 * ## Available Strategies
 *
 * - [Default] – (Default) Generates instants across the full [Instant] range with a random [ZoneId].
 * - [NearPast] – Generates instants from 10 years before now until now.
 * - [NearFuture] – Generates instants from now until 10 years after now.
 * - [DistantPast] – Generates instants from [Instant.MIN] until now.
 * - [DistantFuture] – Generates instants from now until [Instant.MAX].
 * - [Range] – Generates instants within user-defined bounds, optionally pinned to a specific [ZoneId].
 *
 * ## Example Usage
 *
 * ```kotlin
 * // Default full-range behavior
 * some { strategy(ZonedDateTimeStrategy.Default) }
 *
 * // Values from the last 10 years
 * some { strategy(ZonedDateTimeStrategy.NearPast) }
 *
 * // Custom range with a random ZoneId
 * some { strategy(ZonedDateTimeStrategy.Range(min, max)) }
 *
 * // Custom range pinned to a specific ZoneId
 * some { strategy(ZonedDateTimeStrategy.Range(min, max, ZoneId.of("America/Sao_Paulo"))) }
 * ```
 */
sealed interface ZonedDateTimeStrategy : Strategy {
    override val key get() = ZonedDateTimeStrategy::class

    /**
     * Generates instants across the full [Instant] range with a random [ZoneId].
     *
     * This is the default strategy and matches the historical behavior before
     * [ZonedDateTimeStrategy] was introduced.
     */
    data object Default : ZonedDateTimeStrategy

    /**
     * Generates instants from 10 years before now until now.
     */
    data object NearPast : ZonedDateTimeStrategy

    /**
     * Generates instants from now until 10 years after now.
     */
    data object NearFuture : ZonedDateTimeStrategy

    /**
     * Generates instants from [Instant.MIN] until now.
     */
    data object DistantPast : ZonedDateTimeStrategy

    /**
     * Generates instants from now until [Instant.MAX].
     */
    data object DistantFuture : ZonedDateTimeStrategy

    /**
     * Generates instants within the inclusive range [[min], [max]], optionally pinned to [zoneId].
     *
     * When [zoneId] is `null`, a random available JVM [ZoneId] is used.
     *
     * @property min The inclusive lower bound of generated instants.
     * @property max The inclusive upper bound of generated instants.
     * @property zoneId The fixed zone to use, or `null` for a random zone.
     *
     * @throws IllegalArgumentException if [min] is greater than [max].
     */
    data class Range(
        val min: Instant,
        val max: Instant,
        val zoneId: ZoneId? = null,
    ) : ZonedDateTimeStrategy {
        init {
            require(min <= max) {
                "ZonedDateTimeStrategy.Range requires min <= max, but got min=$min and max=$max"
            }
        }
    }

    companion object {
        /**
         * The default [ZonedDateTimeStrategy].
         */
        val default: ZonedDateTimeStrategy get() = Default
    }
}
