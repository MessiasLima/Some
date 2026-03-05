package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.StringStrategy
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class StringResolver(
    private val stringStrategy: StringStrategy = StringStrategy.Random,
    val random: Random
) : TypeResolver {
    override fun canResolve(type: KType): Boolean = type == typeOf<String>()

    override fun resolve(type: KType, chain: ResolverChain): Any {
        return when (stringStrategy) {
            StringStrategy.Random -> generateRandomString()
            StringStrategy.Uuid -> java.util.UUID.randomUUID().toString()
            StringStrategy.Readable -> generateReadableString()
        }
    }

    private fun generateRandomString(): String {
        val length = 8
        return (1..length).map {
            ('a'..'z').random(random)
        }.joinToString("")
    }

    private fun generateReadableString(): String {
        return "string-${random.nextInt(10000)}"
    }
}
