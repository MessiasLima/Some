package dev.appoutlet.some.compat.kotlinfixture

import dev.appoutlet.some.config.NullableStrategy

/**
 * Compatibility nullability strategies matching Appmattus style.
 */
sealed interface NullabilityStrategy {
    fun toSomeStrategy(): NullableStrategy

    data object NeverNullStrategy : NullabilityStrategy {
        override fun toSomeStrategy(): NullableStrategy = NullableStrategy.NeverNull
    }

    data object AlwaysNullStrategy : NullabilityStrategy {
        override fun toSomeStrategy(): NullableStrategy = NullableStrategy.AlwaysNull
    }

    data class RandomlyNullStrategy(val nullability: Float = 0.5f) : NullabilityStrategy {
        init {
            require(nullability in 0f..1f) { "nullability must be between 0.0 and 1.0" }
        }

        override fun toSomeStrategy(): NullableStrategy = NullableStrategy.Random(nullability.toDouble())
    }
}
