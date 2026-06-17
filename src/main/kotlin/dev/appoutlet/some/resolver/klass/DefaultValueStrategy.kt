package dev.appoutlet.some.resolver.klass

import dev.appoutlet.some.core.Strategy
import kotlin.reflect.KClass

/**
 * Strategy for handling optional data class parameters.
 *
 * - **UseDefault** – respects Kotlin's constructor defaults; fixture values are only generated
 *   for required parameters.
 * - **Generate** – overrides defaults with generated fixture values for all parameters.
 *
 * Example usage:
 * ```kotlin
 * someSetup {
 *     strategy(DefaultValueStrategy.Generate)
 * }
 * ```
 */
sealed interface DefaultValueStrategy : Strategy {
    override val key: KClass<out Strategy> get() = DefaultValueStrategy::class

    /**
     * Respects Kotlin's constructor defaults.
     */
    data object UseDefault : DefaultValueStrategy

    /**
     * Overrides defaults with generated fixture values.
     */
    data object Generate : DefaultValueStrategy

    companion object {
        /**
         * The default [DefaultValueStrategy].
         */
        val default: DefaultValueStrategy get() = UseDefault
    }
}
