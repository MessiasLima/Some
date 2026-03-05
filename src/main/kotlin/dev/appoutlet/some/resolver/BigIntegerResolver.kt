package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.reflect.KType
import java.math.BigInteger

public class BigIntegerResolver : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        return type.toString().contains("BigInteger")
    }

    override fun resolve(type: KType, context: FixtureContext, chain: ResolverChain): Any {
        return BigInteger.valueOf(context.random.nextLong())
    }
}
