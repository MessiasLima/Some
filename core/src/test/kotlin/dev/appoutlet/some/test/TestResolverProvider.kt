package dev.appoutlet.some.test

import com.fueledbycaffeine.autoservice.AutoService
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.ResolverProvider
import dev.appoutlet.some.core.StrategyProvider
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Test type handled by the discovered [TestResolverProvider].
 */
data class DiscoveredType(val value: String)

/**
 * Test [ResolverProvider] registered via `autoservice-ir` to verify ServiceLoader discovery.
 */
@AutoService
class TestResolverProvider : ResolverProvider {
    override fun createResolvers(
        strategyProvider: StrategyProvider,
        random: Random,
    ): List<Resolver> = listOf(TestResolver())
}

/**
 * Resolver that handles [DiscoveredType] for discovery tests.
 */
class TestResolver : Resolver {
    override fun canResolve(type: KType): Boolean = type == typeOf<DiscoveredType>()

    override fun resolve(type: KType, chain: ResolverChain): Any? = DiscoveredType("discovered")
}
