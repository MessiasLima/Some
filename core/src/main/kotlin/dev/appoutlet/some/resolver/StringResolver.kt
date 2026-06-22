package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.StringStrategy
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.StrategyProvider
import dev.appoutlet.some.core.get
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Resolves [String] types using the active [StringStrategy].
 *
 * @param strategyProvider Provider of all configured generation strategies.
 * @param random Random source used when generating random strings.
 */
@OptIn(ExperimentalUuidApi::class)
class StringResolver(
    strategyProvider: StrategyProvider,
    private val random: Random
) : Resolver {
    private val stringStrategy = strategyProvider.get<StringStrategy>() ?: StringStrategy.default

    override fun canResolve(type: KType): Boolean = type == typeOf<String>()

    override fun resolve(type: KType, chain: ResolverChain): Any {
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
