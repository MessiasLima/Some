package dev.appoutlet.some.test

import com.fueledbycaffeine.autoservice.AutoService
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.StrategyProvider
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.TypeResolverProvider
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Test type handled by the discovered [TestTypeResolverProvider].
 */
data class DiscoveredType(val value: String)

/**
 * Test [TypeResolverProvider] registered via `autoservice-ir` to verify ServiceLoader discovery.
 */
@AutoService
class TestTypeResolverProvider : TypeResolverProvider {
    override fun createResolvers(
        strategyProvider: StrategyProvider,
        random: Random,
    ): List<TypeResolver> = listOf(TestTypeResolver())
}

/**
 * Resolver that handles [DiscoveredType] for discovery tests.
 */
class TestTypeResolver : TypeResolver {
    override fun canResolve(type: KType): Boolean = type == typeOf<DiscoveredType>()

    override fun resolve(type: KType, chain: ResolverChain): Any? = DiscoveredType("discovered")
}
