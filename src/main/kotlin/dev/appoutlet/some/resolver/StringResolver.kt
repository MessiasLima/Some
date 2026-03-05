package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.StringStrategy
import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class StringResolver : TypeResolver {
    override fun canResolve(type: KType): Boolean = type == typeOf<String>()

    override fun resolve(type: KType, context: FixtureContext, chain: ResolverChain): Any {
        return when (context.config.stringStrategy) {
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
        val counter = context.config.toString().hashCode() // This won't work well
        // Need a better approach - let's use a simple counter in the context
        return "string-${context.random.nextInt(10000)}"
    }
}
