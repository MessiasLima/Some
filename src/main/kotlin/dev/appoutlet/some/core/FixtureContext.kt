package dev.appoutlet.some.core

import dev.appoutlet.some.config.CollectionStrategy
import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.config.StringStrategy
import kotlin.random.Random
import kotlin.reflect.KType

/**
 * Context object provided to custom factory functions.
 * Contains all runtime state and configuration needed for fixture generation.
 */
data class FixtureContext(
    val random: Random,
    val resolutionStack: List<KType>,
    val nullableStrategy: NullableStrategy,
    val stringStrategy: StringStrategy,
    val collectionStrategy: CollectionStrategy
)
