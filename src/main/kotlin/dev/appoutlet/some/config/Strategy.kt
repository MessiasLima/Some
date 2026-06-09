package dev.appoutlet.some.config

/**
 * Marker interface for strategy objects that configure fixture generation behavior.
 *
 * All strategy types ([NullableStrategy], [StringStrategy], [CollectionStrategy],
 * and [DefaultValueStrategy]) implement this interface. Custom strategies can also be
 * created by implementing [Strategy] and registering them via
 * [SomeConfigBuilder.strategy] or [SomeConfig.strategy].
 *
 * Strategies are stored in a [dev.appoutlet.some.core.StrategyProvider] and retrieved
 * by their base type using [dev.appoutlet.some.core.StrategyProvider.get].
 *
 * ## Registering a custom strategy
 *
 * ```kotlin
 * someSetup {
 *     strategy(MyCustomStrategy())
 * }
 * ```
 *
 * ## Retrieving a strategy from a [dev.appoutlet.some.core.FixtureContext]
 *
 * ```kotlin
 * val myStrategy = strategyProvider[MyCustomStrategy::class]
 * ```
 */
interface Strategy
