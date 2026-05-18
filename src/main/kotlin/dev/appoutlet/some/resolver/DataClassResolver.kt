package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.CollectionStrategy
import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.config.StringStrategy
import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.TypeResolver
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor

class DataClassResolver(
    private val propertyFactories: Map<Pair<KClass<*>, String>, FixtureContext.() -> Any?> = emptyMap(),
    private val random: Random = Random.Default,
    private val nullableStrategy: NullableStrategy = NullableStrategy.NullOnCircularReference,
    private val stringStrategy: StringStrategy = StringStrategy.Random(),
    private val collectionStrategy: CollectionStrategy = CollectionStrategy(),
) : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        val kClass = type.classifier as? KClass<*> ?: return false

        return when {
            kClass.primaryConstructor == null -> false

            kClass.isSubclassOf(List::class) || kClass.isSubclassOf(Set::class) || kClass.isSubclassOf(Map::class) -> {
                false
            }

            kClass.isSubclassOf(String::class) -> false

            kClass.isSubclassOf(Number::class) -> false

            kClass.isSubclassOf(Boolean::class) -> false

            kClass.isSubclassOf(Char::class) -> false

            else -> true
        }
    }

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val kClass = type.classifier as KClass<*>
        val constructor = kClass.primaryConstructor
            ?: error("No primary constructor found for ${kClass.simpleName}")

        val typeArgMap = buildTypeArgMap(kClass, type)

        val context = FixtureContext(
            random = random,
            resolutionStack = chain.stack,
            nullableStrategy = nullableStrategy,
            stringStrategy = stringStrategy,
            collectionStrategy = collectionStrategy
        )

        val args = constructor.parameters
            .mapNotNull { param ->
                val propertyFactory = propertyFactories[kClass to param.name]
                if (propertyFactory != null) {
                    param to propertyFactory(context)
                } else if (!param.isOptional) {
                    val paramType = param.type
                    val resolvedType = typeArgMap[paramType.toString()] ?: paramType
                    param to chain.resolve(resolvedType)
                } else {
                    null
                }
            }.toMap()

        return constructor.callBy(args)
    }

    private fun buildTypeArgMap(kClass: KClass<*>, type: KType): Map<String, KType> {
        return kClass.typeParameters
            .zip(type.arguments)
            .associate { (param, projection) ->
                param.name to (projection.type ?: param.createType(emptyList(), false))
            }
    }
}
