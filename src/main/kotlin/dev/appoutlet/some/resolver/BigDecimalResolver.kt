package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.reflect.KType
import java.math.BigDecimal

class BigDecimalResolver : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        return type.toString().contains("BigDecimal")
    }

    override fun resolve(type: KType, context: FixtureContext, chain: ResolverChain): Any {
        return BigDecimal(context.random.nextLong())
    }
}
