package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.TypeResolver
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class NumberResolver(
    private val random: Random
) : TypeResolver {
    override fun canResolve(type: KType): Boolean = type == typeOf<Number>()

    override fun resolve(type: KType, chain: ResolverChain): Any? {
        val types = listOf(
            typeOf<Int>(),
            typeOf<Long>(),
            typeOf<Double>(),
            typeOf<Float>(),
            typeOf<Short>(),
            typeOf<Byte>()
        )
        val selectedType = types.random(random)
        return chain.resolve(selectedType)
    }
}
