package dev.appoutlet.some.core

import dev.appoutlet.some.config.CollectionStrategy
import dev.appoutlet.some.config.DefaultValueStrategy
import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.config.StringStrategy
import kotlin.random.Random
import kotlin.reflect.KType

/**
 * Runtime context provided as the receiver for custom factory functions.
 *
 * Both type factories registered with `register` and property factories registered with `property` receive this
 * context. It exposes the same random source and generation strategies used by the resolver chain so custom values
 * can stay consistent with the active [dev.appoutlet.some.config.SomeConfig].
 *
 * The context is a snapshot for the current factory invocation. In particular, [resolutionStack] is immutable from the
 * factory's perspective and should be used only for inspection or debugging, not for controlling resolver state.
 *
 * @property random Random source for factory-generated values. This respects the configured seed when one is set.
 * @property resolutionStack Types currently being resolved, ordered from the outermost request to the current type.
 * @property nullableStrategy Strategy currently used for nullable type handling.
 * @property stringStrategy Strategy currently used for generated string values.
 * @property collectionStrategy Strategy currently used for generated collection sizes.
 * @property defaultValueStrategy Strategy currently used for handling constructor defaults.
 */
data class FixtureContext(
    val random: Random,
    val resolutionStack: List<KType>,
    val nullableStrategy: NullableStrategy,
    val stringStrategy: StringStrategy,
    val collectionStrategy: CollectionStrategy,
    val defaultValueStrategy: DefaultValueStrategy
)
