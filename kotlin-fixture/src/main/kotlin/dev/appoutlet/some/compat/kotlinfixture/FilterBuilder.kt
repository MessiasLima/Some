package dev.appoutlet.some.compat.kotlinfixture

/**
 * DSL builder for configuring type filters.
 */
class FilterBuilder<T> {
    internal val predicates = mutableListOf<(T) -> Boolean>()
    internal var isDistinct: Boolean = false

    /**
     * Adds a filter predicate that candidates must satisfy.
     */
    fun filter(predicate: (T) -> Boolean) {
        predicates.add(predicate)
    }

    /**
     * Specifies that generated values for this type must be distinct.
     */
    fun distinct() {
        isDistinct = true
    }

    internal fun build(): FilterSpec<T> = FilterSpec(predicates.toList(), isDistinct)
}

/**
 * Specification for filtered type generation.
 */
data class FilterSpec<T>(
    val predicates: List<(T) -> Boolean>,
    val isDistinct: Boolean
) {
    fun matchesPredicate(value: T): Boolean = predicates.all { it(value) }

    fun matches(value: T, seen: Set<T>): Boolean {
        return matchesPredicate(value) && (!isDistinct || value !in seen)
    }
}
