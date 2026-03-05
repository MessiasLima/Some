package dev.appoutlet.some.core

import dev.appoutlet.some.config.SomeConfig
import kotlin.random.Random
import kotlin.reflect.KType

public data class FixtureContext(
    public val random: Random,
    public val resolutionStack: List<KType> = emptyList(),
    public val config: SomeConfig = SomeConfig()
) {
    public fun push(type: KType): FixtureContext = copy(resolutionStack = resolutionStack + type)
    public fun isCircular(type: KType): Boolean = resolutionStack.any { it == type }
}
