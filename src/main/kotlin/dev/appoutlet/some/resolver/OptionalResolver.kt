package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.StrategyProvider
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.get
import dev.appoutlet.some.exception.SomeCircularReferenceException
import java.util.Optional
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf

/**
 * Resolves [Optional] types according to the configured [NullableStrategy].
 *
 * - **AlwaysNull** – always returns [Optional.empty].
 * - **NeverNull** – always resolves a present [Optional].
 * - **Random** – returns [Optional.empty] based on the strategy's probability.
 * - **NullOnCircularReference** – returns [Optional.empty] if a circular reference is detected for the optional field.
 *
 * @param strategyProvider Provider of all configured generation strategies.
 * @param random Random source used by [NullableStrategy.Random].
 */
class OptionalResolver(
    strategyProvider: StrategyProvider,
    private val random: Random
) : TypeResolver {
    /**
     * Resolved nullable strategy used to decide whether the resulting [Optional] is empty or present.
     *
     * Fetched from [strategyProvider], falling back to [NullableStrategy.default] when not registered.
     */
    private val nullableStrategy = strategyProvider.get<NullableStrategy>() ?: NullableStrategy.default

    /**
     * Returns `true` when [type] is a subclass of [Optional].
     *
     * @param type The type to inspect.
     * @return `true` if the type's classifier is a [KClass] that extends [Optional], `false` otherwise.
     */
    override fun canResolve(type: KType): Boolean {
        val kClass = type.classifier as? KClass<*> ?: return false
        return kClass.isSubclassOf(Optional::class)
    }

    /**
     * Resolves an [Optional] of the wrapped type described by [type].
     *
     * The behavior depends on the configured [nullableStrategy]:
     * - [NullableStrategy.AlwaysNull] – always returns [Optional.empty].
     * - [NullableStrategy.NeverNull] – always returns a present [Optional] containing the resolved value.
     * - [NullableStrategy.Random] – returns [Optional.empty] with probability defined by the strategy.
     * - [NullableStrategy.NullOnCircularReference] – resolves the wrapped type and returns it inside a
     *   present [Optional]. If the chain throws [SomeCircularReferenceException], returns [Optional.empty].
     *
     * @param type The [Optional] type to resolve. Its first type argument is used as the wrapped value type.
     * @param chain Resolver chain used to generate the wrapped value.
     * @return An [Optional] that may be empty or present depending on the active strategy.
     * @throws IllegalArgumentException If the [Optional] type uses a star projection.
     */
    override fun resolve(type: KType, chain: ResolverChain): Any {
        val valueType = requireNotNull(type.arguments.firstOrNull()?.type) {
            "Star projection not supported in Optional"
        }

        return when (nullableStrategy) {
            is NullableStrategy.AlwaysNull -> Optional.empty<Any>()
            is NullableStrategy.NeverNull -> Optional.ofNullable(chain.resolve(valueType))
            is NullableStrategy.Random -> {
                if (random.nextDouble() < nullableStrategy.probability) {
                    Optional.empty<Any>()
                } else {
                    Optional.ofNullable(chain.resolve(valueType))
                }
            }
            is NullableStrategy.NullOnCircularReference -> {
                try {
                    Optional.ofNullable(chain.resolve(valueType))
                } catch (_: SomeCircularReferenceException) {
                    Optional.empty<Any>()
                }
            }
        }
    }
}
