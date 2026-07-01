package dev.appoutlet.some.android.resolver

import android.net.Uri
import dev.appoutlet.some.android.strategy.UriStrategy
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.StrategyProvider
import dev.appoutlet.some.core.get
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

private const val CONTENT_SCHEME = "content"
private const val FILE_SCHEME = "file"
private const val HTTPS_SCHEME = "https"
private const val SCHEME_SEPARATOR = "://"
private const val PATH_PREFIX = "/"
private const val QUERY_PREFIX = "?"
private const val QUERY_PAIR_SEPARATOR = "&"
private const val FRAGMENT_PREFIX = "#"

private val ALLOWED_SCHEMES = listOf(CONTENT_SCHEME, FILE_SCHEME, HTTPS_SCHEME)

private val URL_COMPATIBLE_CHARS = ('a'..'z') + ('0'..'9')

private const val MIN_HOST_LENGTH = 3
private const val MAX_HOST_LENGTH = 8
private const val MIN_TLD_LENGTH = 2
private const val MAX_TLD_LENGTH = 3
private const val MIN_PATH_SEGMENTS = 1
private const val MAX_PATH_SEGMENTS = 4
private const val MIN_SEGMENT_LENGTH = 2
private const val MAX_SEGMENT_LENGTH = 6
private const val MIN_QUERY_PAIRS = 1
private const val MAX_QUERY_PAIRS = 3
private const val MIN_QUERY_KEY_LENGTH = 2
private const val MAX_QUERY_KEY_LENGTH = 5
private const val MIN_QUERY_VALUE_LENGTH = 2
private const val MAX_QUERY_VALUE_LENGTH = 5
private const val MIN_FRAGMENT_LENGTH = 2
private const val MAX_FRAGMENT_LENGTH = 6

/**
 * Resolves [Uri] types using the active [UriStrategy].
 *
 * @param strategyProvider Provider of all configured generation strategies.
 * @param random Random source used when generating random URI components.
 */
class UriResolver(
    strategyProvider: StrategyProvider,
    private val random: Random
) : Resolver {
    private val uriStrategy = strategyProvider.get<UriStrategy>() ?: UriStrategy.default

    override fun canResolve(type: KType): Boolean = type == typeOf<Uri>()

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val scheme = when (uriStrategy) {
            is UriStrategy.Random -> ALLOWED_SCHEMES.random(random)
            is UriStrategy.Content -> CONTENT_SCHEME
            is UriStrategy.File -> FILE_SCHEME
            is UriStrategy.Url -> HTTPS_SCHEME
        }

        val host = generateHost()
        val path = generatePath()
        val query = if (random.nextBoolean()) generateQuery() else null
        val fragment = if (random.nextBoolean()) generateFragment() else null

        val builder = StringBuilder()
            .append(scheme)
            .append(SCHEME_SEPARATOR)
            .append(host)
            .append(path)

        if (query != null) {
            builder.append(QUERY_PREFIX).append(query)
        }

        if (fragment != null) {
            builder.append(FRAGMENT_PREFIX).append(fragment)
        }

        return Uri.parse(builder.toString())
    }

    private fun generateHost(): String {
        return if (uriStrategy is UriStrategy.Url) {
            val host = urlCompatibleString(MIN_HOST_LENGTH, MAX_HOST_LENGTH)
            val tld = urlCompatibleString(MIN_TLD_LENGTH, MAX_TLD_LENGTH)
            "$host.$tld"
        } else {
            urlCompatibleString(MIN_HOST_LENGTH, MAX_HOST_LENGTH)
        }
    }

    private fun generatePath(): String {
        val segments = random.nextInt(MIN_PATH_SEGMENTS, MAX_PATH_SEGMENTS)
        return List(segments) { urlCompatibleString(MIN_SEGMENT_LENGTH, MAX_SEGMENT_LENGTH) }
            .joinToString(prefix = PATH_PREFIX, separator = PATH_PREFIX)
    }

    private fun generateQuery(): String {
        val pairs = random.nextInt(MIN_QUERY_PAIRS, MAX_QUERY_PAIRS)

        return List(pairs) {
            val key = urlCompatibleString(MIN_QUERY_KEY_LENGTH, MAX_QUERY_KEY_LENGTH)
            val value = urlCompatibleString(MIN_QUERY_VALUE_LENGTH, MAX_QUERY_VALUE_LENGTH)
            "$key=$value"
        }.joinToString(QUERY_PAIR_SEPARATOR)
    }

    private fun generateFragment(): String {
        return urlCompatibleString(MIN_FRAGMENT_LENGTH, MAX_FRAGMENT_LENGTH)
    }

    private fun urlCompatibleString(minLength: Int, maxLength: Int): String {
        val length = random.nextInt(minLength, maxLength)
        return List(length) { URL_COMPATIBLE_CHARS.random(random) }.joinToString("")
    }
}
