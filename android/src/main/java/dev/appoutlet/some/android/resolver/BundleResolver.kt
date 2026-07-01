package dev.appoutlet.some.android.resolver

import android.os.Bundle
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

private const val MIN_ENTRIES = 0
private const val MAX_ENTRIES = 5
private const val MIN_KEY_LENGTH = 1
private const val MAX_KEY_LENGTH = 10

/**
 * Resolves [Bundle] types by creating a new [Bundle] and populating it with
 * random entries.
 *
 * Generated bundles contain 0 to 5 entries. Each entry has a non-empty string
 * key and a value of type [String], [Int], [Long], [Float], [Double], or
 * [Boolean].
 *
 * @param random Random source used when generating entry count, keys, and
 * values.
 */
class BundleResolver(
    private val random: Random
) : Resolver {
    private val valueGenerators: List<() -> Any> = listOf(
        { generateString() },
        { random.nextInt() },
        { random.nextLong() },
        { random.nextFloat() },
        { random.nextDouble() },
        { random.nextBoolean() }
    )

    override fun canResolve(type: KType): Boolean = type == typeOf<Bundle>()

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val bundle = Bundle()
        val entryCount = random.nextInt(MIN_ENTRIES, MAX_ENTRIES + 1)

        repeat(entryCount) {
            val key = generateKey()
            val value = valueGenerators.random(random).invoke()
            putValue(bundle, key, value)
        }

        return bundle
    }

    private fun generateKey(): String {
        val length = random.nextInt(MIN_KEY_LENGTH, MAX_KEY_LENGTH + 1)
        return List(length) { ('a'..'z').random(random) }.joinToString("")
    }

    private fun generateString(): String {
        val length = random.nextInt(MIN_KEY_LENGTH, MAX_KEY_LENGTH + 1)
        return List(length) { ('a'..'z').random(random) }.joinToString("")
    }

    private fun putValue(bundle: Bundle, key: String, value: Any) {
        when (value) {
            is String -> bundle.putString(key, value)
            is Int -> bundle.putInt(key, value)
            is Long -> bundle.putLong(key, value)
            is Float -> bundle.putFloat(key, value)
            is Double -> bundle.putDouble(key, value)
            is Boolean -> bundle.putBoolean(key, value)
        }
    }
}
