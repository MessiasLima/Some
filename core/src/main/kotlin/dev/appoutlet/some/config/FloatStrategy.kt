package dev.appoutlet.some.config

/**
 * Strategy for generating float values.
 *
 * @property range The range within which float values will be generated (default 0.0f..1.0f).
 */
data class FloatStrategy(
    val range: ClosedFloatingPointRange<Float> = 0.0f..1.0f
) : Strategy {
    override val key = FloatStrategy::class

    init {
        require(range.start <= range.endInclusive) { "range.start must be less than or equal to range.endInclusive" }
    }

    /**
     * Convenience constructor for a fixed float value.
     */
    constructor(fixed: Float) : this(fixed..fixed)

    companion object {
        /**
         * The default float strategy.
         */
        val default: FloatStrategy get() = FloatStrategy()
    }
}
