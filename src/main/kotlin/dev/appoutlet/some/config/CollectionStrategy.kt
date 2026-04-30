package dev.appoutlet.some.config

private const val DEFAULT_SIZE_RANGE_START = 1
private const val DEFAULT_SIZE_RANGE_END = 5
private val defaultSizeRange = DEFAULT_SIZE_RANGE_START..DEFAULT_SIZE_RANGE_END

data class CollectionStrategy(
    val sizeRange: IntRange = defaultSizeRange
) {
    init {
        require(sizeRange.first > -1) { "sizeRange.start must be positive" }
        require(sizeRange.last > sizeRange.first) { "sizeRange.end must be greater than or equal to sizeRange.start" }
    }
}
