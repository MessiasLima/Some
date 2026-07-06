package dev.appoutlet.some.android.resolver

import android.os.Bundle
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

private const val MIN_STRING_LENGTH = 1
private const val MAX_STRING_LENGTH = 10

/**
 * Resolves [Bundle] types by creating a new [Bundle] and populating it with
 * one entry for each supported primitive value type.
 *
 * Generated bundles always contain one [String], [Int], [Long], [Float],
 * [Double], and [Boolean] entry with random values.
 *
 * @param random Random source used when generating values.
 */
class BundleResolver(
    private val random: Random
) : Resolver {
    override fun canResolve(type: KType): Boolean = type == typeOf<Bundle>()

    override fun resolve(type: KType, chain: ResolverChain): Any {
        return Bundle().apply {
            putString("string", generateString())
            putInt("int", random.nextInt())
            putLong("long", random.nextLong())
            putFloat("float", random.nextFloat())
            putDouble("double", random.nextDouble())
            putBoolean("boolean", random.nextBoolean())
        }
    }

    private fun generateString(): String {
        val length = random.nextInt(MIN_STRING_LENGTH, MAX_STRING_LENGTH + 1)
        return List(length) { ('a'..'z').random(random) }.joinToString("")
    }
}
