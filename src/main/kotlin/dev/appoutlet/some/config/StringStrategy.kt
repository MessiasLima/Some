package dev.appoutlet.some.config

/**
 * Strategy for generating string values.
 *
 * Available strategies:
 * - **Random** – generates random lowercase alphabetic strings with configurable length (default 8)
 * - **Uuid** – generates UUID strings
 * - **Readable** – generates human-readable strings like "string-1234"
 *
 * Example usage:
 * ```kotlin
 * val config = SomeConfig().apply {
 *     stringStrategy = StringStrategy.Random(length = 16)
 * }
 * val some = someSetup { stringStrategy = StringStrategy.Uuid }
 * ```
 */
sealed interface StringStrategy {
    /**
     * Generates random lowercase alphabetic strings.
     * @param length The length of the generated string (default 8)
     */
    data class Random(val length: Int = 8) : StringStrategy {
        init {
            require(length > 0) { "Length must be greater than 0" }
        }
    }
    
    /**
     * Generates UUID strings.
     */
    data object Uuid : StringStrategy
    
    /**
     * Generates human-readable strings like "string-1234".
     */
    data object Readable : StringStrategy
}
