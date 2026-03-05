package dev.appoutlet.some.config

data class CollectionStrategy(
    val sizeRange: IntRange = 1..5
) {
    init {
        require(sizeRange.first > -1) { "sizeRange.start must be positive" }
        require(sizeRange.last > sizeRange.first) { "sizeRange.end must be greater than or equal to sizeRange.start" }
    }
}
