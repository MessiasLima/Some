package dev.appoutlet.some.config

private const val DEFAULT_SIZE_RANGE_START = 1
private const val DEFAULT_SIZE_RANGE_END = 5
private val defaultSizeRange = DEFAULT_SIZE_RANGE_START..DEFAULT_SIZE_RANGE_END

/**
 * Strategy controlling the number of values generated for collections and maps.
 *
 * The configured range is inclusive at both ends. The default generates between one and five values.
 *
 * @property sizeRange Inclusive range of collection sizes. Both bounds must be non-negative and the end must not be
 * less than the start.
 */
data class CollectionStrategy(
    val sizeRange: IntRange = defaultSizeRange
) : Strategy {
    override val key = CollectionStrategy::class

    init {
        require(sizeRange.first > -1) { "sizeRange.start must be positive" }
        require(sizeRange.last >= sizeRange.first) { "sizeRange.end must be greater than or equal to sizeRange.start" }
    }

    /**
     * Creates a strategy that always generates collections with [size] values.
     *
     * @param size Fixed non-negative collection size.
     */
    constructor(size: Int) : this(size..size)

    companion object {
        /**
         * The default collection strategy.
         */
        val default: CollectionStrategy get() = CollectionStrategy()
    }
}
