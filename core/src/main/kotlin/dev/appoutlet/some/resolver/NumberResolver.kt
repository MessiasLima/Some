package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * The concrete numeric types that can be randomly chosen when resolving a [Number] request.
 *
 * This list contains [Int], [Long], [Double], [Float], and [Short]. A random entry is picked
 * on each resolution and delegated back to the [ResolverChain].
 */
private val numberTypes = listOf(
    typeOf<Int>(),
    typeOf<Long>(),
    typeOf<Double>(),
    typeOf<Float>(),
    typeOf<Short>()
)

/**
 * Resolves the abstract [Number] type by randomly picking one of the supported concrete numeric
 * types and delegating its generation back to the [ResolverChain].
 *
 * @param random Random source used to select which concrete numeric type is generated.
 */
class NumberResolver(
    private val random: Random
) : Resolver {

    /**
     * Returns `true` when [type] is exactly [Number].
     *
     * @param type The type to inspect.
     * @return `true` if the type is [Number], `false` otherwise.
     */
    override fun canResolve(type: KType): Boolean = type == typeOf<Number>()

    /**
     * Picks a random concrete numeric type from [numberTypes] and asks [chain] to resolve it.
     *
     * @param type The requested type. Expected to be [Number], but the actual value is produced
     * by delegating to a concrete numeric resolver through [chain].
     * @param chain Resolver chain used to generate the selected concrete numeric type.
     * @return A randomly chosen numeric value among [Int], [Long], [Double], [Float], or [Short].
     */
    override fun resolve(type: KType, chain: ResolverChain): Any? {
        val selectedType = numberTypes.random(random)
        return chain.resolve(selectedType)
    }
}
