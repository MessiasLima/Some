package dev.appoutlet.some.core

import kotlin.random.Random
import kotlin.reflect.KType

/**
 * Runtime context provided as the receiver for custom factory functions.
 *
 * Both type factories registered with `factory` and property factories registered with `property` receive this
 * context. It exposes the same random source and strategy provider used by the resolver chain so custom values can
 * stay consistent with the active [dev.appoutlet.some.config.SomeConfig].
 *
 * The context is a snapshot for the current factory invocation. In particular, [resolutionStack] is immutable from the
 * factory's perspective and should be used only for inspection or debugging, not for controlling resolver state.
 *
 * ## Accessing strategies
 *
 * Use [strategyProvider] to retrieve a registered strategy:
 *
 * ```kotlin
 * factory(MyType::class) {
 *     val stringStrategy = strategyProvider[StringStrategy::class]
 *     MyType(stringStrategy is StringStrategy.Readable)
 * }
 * ```
 *
 * @property random Random source for factory-generated values. This respects the configured seed when one is set.
 * @property resolutionStack Types currently being resolved, ordered from the outermost request to the current type.
 * @property strategyProvider Provides access to all registered generation strategies by their base type.
 */
data class FixtureContext(
    val random: Random,
    val resolutionStack: List<KType>,
    val strategyProvider: StrategyProvider,
)
