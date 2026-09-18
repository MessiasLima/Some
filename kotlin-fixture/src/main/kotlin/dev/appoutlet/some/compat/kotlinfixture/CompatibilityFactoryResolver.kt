package dev.appoutlet.some.compat.kotlinfixture

import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.KType

internal class CompatibilityFactoryResolver(
    private val factories: Map<KType, Generator<Any?>.() -> Any?>,
    private val random: Random,
) : Resolver {
    private val resolvingTypes = ThreadLocal.withInitial { mutableSetOf<KType>() }

    override fun canResolve(type: KType): Boolean {
        return type in factories && type !in resolvingTypes.get()
    }

    override fun resolve(type: KType, chain: ResolverChain): Any? {
        val factory = factories[type] ?: return null
        val activeSet = resolvingTypes.get()

        val generator = object : Generator<Any?> {
            override val random: Random get() = this@CompatibilityFactoryResolver.random

            override fun <R> resolveType(type: KType): R {
                @Suppress("UNCHECKED_CAST")
                return chain.resolve(type) as R
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

        activeSet.add(type)
        try {
            return factory.invoke(generator)
        } finally {
            activeSet.remove(type)
        }
    }
}
