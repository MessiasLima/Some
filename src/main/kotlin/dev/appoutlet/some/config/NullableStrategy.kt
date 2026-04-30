package dev.appoutlet.some.config

/**
 * Strategy for handling nullable types during fixture generation.
 *
 * Determines whether nullable types should be resolved as `null` or as a concrete value.
 *
 * ## Available Strategies
 *
 * - [AlwaysNull] – Always produces `null` for nullable types
 * - [NeverNull] – Always produces non-null values for nullable types
 * - [Random] – Produces `null` based on a configurable probability
 *
 * ## Example Usage
 *
 * ```kotlin
 * // Always generate null values
 * some { nullableStrategy = NullableStrategy.AlwaysNull }
 *
 * // Never generate null values
 * some { nullableStrategy = NullableStrategy.NeverNull }
 *
 * // 50% chance of null (default)
 * some { nullableStrategy = NullableStrategy.Random() }
 *
 * // 80% chance of null
 * some { nullableStrategy = NullableStrategy.Random(probability = 0.8) }
 *
 * // Never generate null (0% chance)
 * some { nullableStrategy = NullableStrategy.Random(probability = 0.0) }
 * ```
 */
sealed interface NullableStrategy {
    /**
     * Always returns `null` for nullable types.
     *
     * Use this strategy when testing edge cases or validating null-handling logic.
     */
    object AlwaysNull : NullableStrategy

    /**
     * Never returns `null` for nullable types – always resolves a concrete non-null value.
     *
     * Use this strategy when you want to ensure all nullable fields are populated,
     * which is useful for testing the "happy path" scenarios.
     */
    object NeverNull : NullableStrategy

    /**
     * Returns `null` based on a configurable probability.
     *
     * @property probability The likelihood of generating `null` for nullable types,
     *                       expressed as a value between 0.0 and 1.0 (inclusive).
     *                       - `0.0` means **never** generate `null` (equivalent to [NeverNull])
     *                       - `1.0` means **always** generate `null` (equivalent to [AlwaysNull])
     *                       - `0.5` (default) means a 50% chance of generating `null`
     *                       - `0.8` means an 80% chance of generating `null`
     *
     * The default probability is `0.5`, which gives an equal chance of null or non-null values,
     * useful for testing a mix of scenarios.
     *
     * ## Examples
     *
     * ```kotlin
     * // 50% chance of null (default)
     * NullableStrategy.Random()
     *
     * // 20% chance of null (mostly non-null values)
     * NullableStrategy.Random(probability = 0.2)
     *
     * // 90% chance of null (mostly null values)
     * NullableStrategy.Random(probability = 0.9)
     * ```
     */
    data class Random(val probability: Double = 0.5) : NullableStrategy
}
