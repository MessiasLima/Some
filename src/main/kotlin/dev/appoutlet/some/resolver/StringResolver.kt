package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.StringStrategy
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.StrategyProvider
import dev.appoutlet.some.core.TypeResolver
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class StringResolver(
    private val strategyProvider: StrategyProvider,
    val random: Random
) : TypeResolver {
    override fun canResolve(type: KType): Boolean = type == typeOf<String>()

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val stringStrategy = strategyProvider[StringStrategy::class]
        return when (stringStrategy) {
            is StringStrategy.Random -> generateRandomString(stringStrategy.length)
            is StringStrategy.Uuid -> Uuid.random().toString()
            is StringStrategy.Readable -> generateReadableString()
        }
    }

    private fun generateRandomString(length: Int): String {
        return (1..length).map {
            ('a'..'z').random(random)
        }.joinToString("")
    }

    private fun generateReadableString(): String {
        return "string-${random.nextInt()}"
    }
}
