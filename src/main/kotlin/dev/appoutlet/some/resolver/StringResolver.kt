package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.StringStrategy
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.TypeResolver
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Resolves [String] types using the active [StringStrategy].
 *
 * @param stringStrategy Strategy for generating string values. Defaults to [StringStrategy.default] when null.
 * @param random Random source used when generating random strings.
 */
@OptIn(ExperimentalUuidApi::class)
class StringResolver(
    stringStrategy: StringStrategy?,
    val random: Random
) : TypeResolver {
    private val stringStrategy = stringStrategy ?: StringStrategy.default

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
