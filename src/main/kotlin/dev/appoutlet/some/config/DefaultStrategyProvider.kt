package dev.appoutlet.some.config

import dev.appoutlet.some.core.Strategy
import dev.appoutlet.some.core.StrategyProvider
import kotlin.reflect.KClass

/**
 * Default [StrategyProvider] implementation backed by a map of registered strategy instances.
 *
 * The provider is intentionally narrow: it exposes only strategy lookup and hides all other configuration
 * details from resolvers.
 *
 * @param strategies Map of strategy instances keyed by their base type.
 */
class DefaultStrategyProvider(
    private val strategies: Map<KClass<out Strategy>, Strategy> = emptyMap()
) : StrategyProvider {
    @Suppress("UNCHECKED_CAST")
    override operator fun <T : Strategy> get(key: KClass<T>): T? = strategies[key] as? T
}
