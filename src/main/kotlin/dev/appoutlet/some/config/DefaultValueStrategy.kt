package dev.appoutlet.some.config

/**
 * Strategy for handling data class constructor parameters with default values.
 *
 * Determines whether the resolver should use the Kotlin default value or generate a new value.
 *
 * ## Available Strategies
 *
 * - [UseDefault] – (Default) Uses the Kotlin default value for optional parameters.
 * - [Generate] – Generates a value for optional parameters through the resolver chain.
 *
 * ## Example Usage
 *
 * ```kotlin
 * // Default behavior: use Kotlin defaults
 * some { strategy(DefaultValueStrategy.UseDefault) }
 *
 * // Always generate values, even for parameters with defaults
 * some { strategy(DefaultValueStrategy.Generate) }
 * ```
 */
sealed interface DefaultValueStrategy : Strategy {
    override val key get() = DefaultValueStrategy::class

    /**
     * Uses the Kotlin default constructor value for optional parameters.
     *
     * This is the default behavior and preserves backward compatibility.
     */
    data object UseDefault : DefaultValueStrategy

    /**
     * Generates a value for optional parameters through the resolver chain.
     *
     * Use this strategy when you want fully populated objects regardless of whether
     * they have default values in their constructor.
     */
    data object Generate : DefaultValueStrategy

    companion object {
        /**
         * The default default-value strategy.
         */
        val default: DefaultValueStrategy get() = UseDefault
    }
}
