package dev.appoutlet.some.core

import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.resolver.nullable.NullableStrategy
import dev.appoutlet.some.resolver.string.StringStrategy
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
 *         val strategy = strategyProvider.get<StringStrategy>()
 *         MyType(strategy is StringStrategy.Readable)
 *     }
 * }
 * ```
 *
 * The operator form is also supported:
 *
 * ```kotlin
 * val strategy = strategyProvider[MyStrategy::class]
 * ```
 */
interface StrategyProvider {
    /**
     * Returns the strategy instance registered for [key], or `null` when no strategy is registered.
     *
     * @param key The [KClass] of the strategy to retrieve
     *   (e.g. [NullableStrategy::class][dev.appoutlet.some.resolver.nullable.NullableStrategy]).
     * @return The registered strategy instance, or `null` when no strategy is registered for [key].
     */
    operator fun <T : Strategy> get(key: KClass<T>): T?
}

/**
 * Returns the strategy instance of type [T] registered for this provider, or `null` when no strategy is registered.
 *
 * This is a convenience extension that allows idiomatic usage without explicitly passing a [KClass]:
 *
 * ```kotlin
 * val strategy = strategyProvider.get<StringStrategy>()
 * ```
 *
 * @param T The type of the strategy to retrieve.
 * @return The registered strategy instance, or `null` when no strategy is registered for [T].
 */
inline fun <reified T : Strategy> StrategyProvider.get(): T? = get(T::class)
