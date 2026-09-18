package dev.appoutlet.some.compat.kotlinfixture

/**
 * Strategy controlling sequence generation length for [Fixture.asSequence].
 */
sealed interface SequenceStrategy {
    /**
     * Generates an unbounded sequence of fixture values.
     */
    data object Unbounded : SequenceStrategy

    /**
     * Generates a sequence bounded to [numberOfElements].
     *
     * @property numberOfElements Exact number of elements the sequence should produce. Must be >= 0.
     */
    data class Bounded(val numberOfElements: Int) : SequenceStrategy {
        init {
            require(numberOfElements >= 0) { "numberOfElements must be >= 0" }
        }
    }
}
