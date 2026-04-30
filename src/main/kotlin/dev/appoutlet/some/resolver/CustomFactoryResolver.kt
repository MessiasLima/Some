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

class CustomFactoryResolver(
    private val factories: Map<KClass<*>, FixtureContext.() -> Any?>,
    private val random: Random,
    private val nullableStrategy: NullableStrategy,
    private val stringStrategy: StringStrategy,
    private val collectionStrategy: CollectionStrategy
) : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        val kClass = type.classifier as? KClass<*> ?: return false
        return kClass in factories
    }

    override fun resolve(type: KType, chain: ResolverChain): Any? {
        val kClass = type.classifier as KClass<*>
        val factory = factories[kClass] ?: return null

        // Bridge: Create FixtureContext for user factories with an immutable copy of the stack
        val context = FixtureContext(
            random = random,
            resolutionStack = chain.stack, // This returns an immutable snapshot
            nullableStrategy = nullableStrategy,
            stringStrategy = stringStrategy,
            collectionStrategy = collectionStrategy
        )

        return factory.invoke(context)
    }
}
