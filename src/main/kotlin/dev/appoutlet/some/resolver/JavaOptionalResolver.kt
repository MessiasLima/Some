package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.TypeResolver
import java.util.Optional
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubclassOf

/**
 * Resolves [Optional] types according to the configured [NullableStrategy].
 *
 * - **AlwaysNull** – always returns [Optional.empty].
 * - **NeverNull** – always resolves a present [Optional].
 * - **Random** – returns [Optional.empty] based on the strategy's probability.
 * - **NullOnCircularReference** – returns [Optional.empty] if a circular reference is detected for the optional field.
 *
 * @param nullableStrategy Strategy for resolving optional types. Defaults to [NullableStrategy.default] when null.
 * @param random Random source used by [NullableStrategy.Random].
 */
class JavaOptionalResolver(
    nullableStrategy: NullableStrategy?,
    private val random: Random
) : TypeResolver {
    private val nullableStrategy = nullableStrategy ?: NullableStrategy.default

    override fun canResolve(type: KType): Boolean {
        val kClass = type.classifier as? KClass<*> ?: return false
        return kClass.isSubclassOf(Optional::class)
    }

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
                val nullableValueType = (valueType.classifier as? KClass<*>)?.createType(
                    arguments = valueType.arguments,
                    nullable = true
                ) ?: valueType

                Optional.ofNullable(chain.resolve(nullableValueType))
            }
        }
    }
}
