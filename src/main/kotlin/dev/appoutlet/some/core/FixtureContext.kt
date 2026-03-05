package dev.appoutlet.some.core

import kotlin.random.Random
import kotlin.reflect.KType

data class FixtureContext(
    val random: Random,
    val resolutionStack: List<KType> = emptyList()
) {
    fun push(type: KType): FixtureContext = copy(resolutionStack = resolutionStack + type)
    fun isCircular(type: KType): Boolean = resolutionStack.any { it == type }
}
