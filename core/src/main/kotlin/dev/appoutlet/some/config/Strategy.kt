package dev.appoutlet.some.config

import kotlin.reflect.KClass

/**
 * Marker interface for strategy objects that configure fixture generation behavior.
 *
 * All built-in strategies ([NullableStrategy], [StringStrategy], [CollectionStrategy],
 * [FloatStrategy], and [DefaultValueStrategy]) implement this interface. Custom strategies can also be
 * created by implementing [Strategy] and registering them via [SomeConfigBuilder.strategy].
 *
 * The [key] property determines the registration bucket for the strategy. For sealed interface
 * strategies, all implementations share their parent interface as the key so that registering
 * any implementation replaces the entire strategy entry. For non-sealed strategies, the key
 * is the class itself.
 *
 * ## Registering a strategy
 *
 * ```kotlin
 * someSetup {
 *     strategy(NullableStrategy.NeverNull)
 *     strategy(StringStrategy.Uuid)
 * }
 * ```
 *
 * ## Retrieving a strategy from a [dev.appoutlet.some.core.StrategyProvider]
 *
 * ```kotlin
 * val myStrategy = strategyProvider[NullableStrategy::class]
 * ```
 */
interface Strategy {
    /**
     * The [KClass] of the base strategy type used as a lookup key.
     */
    val key: KClass<out Strategy>
}
