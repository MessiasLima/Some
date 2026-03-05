package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.StringStrategy
import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class StringResolver(
    private val stringStrategy: StringStrategy = StringStrategy.Random
) : TypeResolver {
    override fun canResolve(type: KType): Boolean = type == typeOf<String>()

    override fun resolve(type: KType, context: FixtureContext, chain: ResolverChain): Any {
        return when (stringStrategy) {
            StringStrategy.Random -> generateRandomString(context)
            StringStrategy.Uuid -> java.util.UUID.randomUUID().toString()
            StringStrategy.Readable -> generateReadableString(context)
        }
    }

    private fun generateRandomString(context: FixtureContext): String {
        val length = 8
        return (1..length).map {
            ('a'..'z').random(context.random)
        }.joinToString("")
    }

    private fun generateReadableString(context: FixtureContext): String {
        return "string-${context.random.nextInt(10000)}"
    }
}
