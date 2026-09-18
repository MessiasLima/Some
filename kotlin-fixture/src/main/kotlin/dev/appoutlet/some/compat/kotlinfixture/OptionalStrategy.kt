package dev.appoutlet.some.compat.kotlinfixture

import dev.appoutlet.some.config.DefaultValueStrategy

/**
 * Compatibility optional value strategies matching Appmattus style.
 */
sealed interface OptionalStrategy {
    fun toSomeStrategy(): DefaultValueStrategy

    data object AlwaysOptionalStrategy : OptionalStrategy {
        override fun toSomeStrategy(): DefaultValueStrategy = DefaultValueStrategy.UseDefault
    }

    data object NeverOptionalStrategy : OptionalStrategy {
        override fun toSomeStrategy(): DefaultValueStrategy = DefaultValueStrategy.Generate
    }

    data class RandomlyOptionalStrategy(val optionality: Float = 0.5f) : OptionalStrategy {
        init {
            require(optionality in 0f..1f) { "optionality must be between 0.0 and 1.0" }
        }

        override fun toSomeStrategy(): DefaultValueStrategy = DefaultValueStrategy.Random(optionality)
    }
}
