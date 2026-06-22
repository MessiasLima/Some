package dev.appoutlet.some.config

data class FloatStrategy(
    val range: ClosedFloatingPointRange<Float> = 0.0f..1.0f
) : Strategy {
    override val key = FloatStrategy::class

    constructor(fixed: Float) : this(fixed..fixed)

    init {
        require(range.start <= range.endInclusive) {
            "range.start must be less than or equal to range.endInclusive"
        }
    }

    companion object {
        /**
         * The default float strategy.
         */
        val default = FloatStrategy()
    }
}
