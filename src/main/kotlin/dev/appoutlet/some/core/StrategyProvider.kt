package dev.appoutlet.some.core

import dev.appoutlet.some.config.Strategy
import kotlin.reflect.KClass

/**
 * Provides strategy instances keyed by their base type.
 *
 * [SomeConfig][dev.appoutlet.some.config.SomeConfig] implements this interface so custom factories can inspect the
 * active configuration through [FixtureContext.strategyProvider].
 *
 * ## Usage in custom type factories
 *
 * Custom factories receive a [FixtureContext][dev.appoutlet.some.core.FixtureContext] whose
 * [FixtureContext.strategyProvider] property implements [StrategyProvider]:
 *
 * ```kotlin
 * someSetup {
 *     factory(MyType::class) {
 *         val strategy = strategyProvider[MyStrategy::class]
 *         MyType(strategy.value)
 *     }
 * }
 * ```
 */
interface StrategyProvider {
    /**
     * Returns the strategy instance registered for [key].
     *
     * @param key The [KClass] of the strategy to retrieve
     *   (e.g. [NullableStrategy::class][dev.appoutlet.some.config.NullableStrategy]).
     * @return The registered strategy instance.
     * @throws NoSuchElementException when no strategy is registered for [key].
     */
    operator fun <T : Strategy> get(key: KClass<T>): T
}
