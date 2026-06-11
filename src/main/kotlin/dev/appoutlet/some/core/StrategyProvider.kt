package dev.appoutlet.some.core

import dev.appoutlet.some.config.Strategy
import kotlin.reflect.KClass

/**
 * Provides access to configured [Strategy] instances.
 */
interface StrategyProvider {
    /**
     * Retrieves the [Strategy] of type [key].
     *
     * @throws IllegalArgumentException if the strategy is not registered.
     */
    operator fun <T : Strategy> get(key: KClass<T>): T
}
