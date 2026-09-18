package dev.appoutlet.some.compat.kotlinfixture

import dev.appoutlet.some.config.NullableStrategy

/**
 * Compatibility recursion strategies matching Appmattus style.
 */
sealed interface RecursionStrategy {
    fun toSomeStrategy(): NullableStrategy

    data object NullRecursionStrategy : RecursionStrategy {
        override fun toSomeStrategy(): NullableStrategy = NullableStrategy.NullOnCircularReference
    }

    data object ThrowingRecursionStrategy : RecursionStrategy {
        override fun toSomeStrategy(): NullableStrategy = NullableStrategy.NeverNull
    }

    data object UnresolvedRecursionStrategy : RecursionStrategy {
        override fun toSomeStrategy(): NullableStrategy = NullableStrategy.NeverNull
    }
}
