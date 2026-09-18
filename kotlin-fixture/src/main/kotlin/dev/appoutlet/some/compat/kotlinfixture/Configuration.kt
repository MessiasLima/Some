package dev.appoutlet.some.compat.kotlinfixture

import dev.appoutlet.some.config.DefaultValueStrategy
import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.core.FixtureContext
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Immutable configuration for compatibility fixture generation.
 */
data class Configuration(
    val random: Random = Random.Default,
    val typeFactories: Map<KType, Generator<Any?>.() -> Any?> = emptyMap(),
    val propertyFactories: Map<Pair<KClass<*>, String>, Generator<Any?>.() -> Any?> = emptyMap(),
    val subTypes: Map<KClass<*>, KClass<*>> = emptyMap(),
    val filters: Map<KType, FilterSpec<Any?>> = emptyMap(),
    val nullabilityStrategy: NullabilityStrategy = NullabilityStrategy.NeverNullStrategy,
    val optionalStrategy: OptionalStrategy = OptionalStrategy.AlwaysOptionalStrategy,
    val recursionStrategy: RecursionStrategy = RecursionStrategy.NullRecursionStrategy,
) {
    internal fun toSomeConfig(): SomeConfig {
        val effectiveNullableStrategy: NullableStrategy = when {
            recursionStrategy is RecursionStrategy.NullRecursionStrategy &&
                nullabilityStrategy is NullabilityStrategy.NeverNullStrategy -> NullableStrategy.NullOnCircularReference
            else -> nullabilityStrategy.toSomeStrategy()
        }

        val mappedPropertyFactories = propertyFactories.mapValues { (_, generatorLambda) ->
            val contextLambda: FixtureContext.() -> Any? = {
                val ctx = this
                val generator = object : Generator<Any?> {
                    override val random: Random get() = ctx.random

                    override fun <R> resolveType(type: KType): R {
                        val res = ctx.resolver ?: error("Resolver not available in FixtureContext")
                        @Suppress("UNCHECKED_CAST")
                        return res(type) as R
                    }

                    override fun <R> resolveRange(iterable: Iterable<R>, type: KType): R {
                        val list = iterable.toList()
                        return if (list.isNotEmpty()) {
                            list[random.nextInt(list.size)]
                        } else {
                            resolveType(type)
                        }
                    }
                }
                generatorLambda(generator)
            }
            contextLambda
        }

        return SomeConfig(
            strategies = mapOf(
                NullableStrategy::class to effectiveNullableStrategy,
                DefaultValueStrategy::class to optionalStrategy.toSomeStrategy(),
            ),
            propertyFactories = mappedPropertyFactories,
        )
    }
}
