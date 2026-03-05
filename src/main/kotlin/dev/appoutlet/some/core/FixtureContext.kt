package dev.appoutlet.some.core

import dev.appoutlet.some.config.SomeConfig
import kotlin.random.Random
import kotlin.reflect.KType

data class FixtureContext(
    val random: Random,
    val resolutionStack: List<KType> = emptyList(),
    val config: SomeConfig = SomeConfig()
) {
    fun push(type: KType): FixtureContext = copy(resolutionStack = resolutionStack + type)
    fun isCircular(type: KType): Boolean = resolutionStack.any { it == type }
}
