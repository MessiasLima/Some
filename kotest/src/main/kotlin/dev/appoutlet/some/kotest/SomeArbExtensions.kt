package dev.appoutlet.some.kotest

import dev.appoutlet.some.config.SomeConfigBuilder
import dev.appoutlet.some.someSetup
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample

/**
 * Creates a Kotest [Arb] that generates values of type [T] using `Some`.
 *
 * Each call to [sample][Arb.sample] builds a fresh `Some` generator seeded with the
 * Kotest [RandomSource.seed], applies the supplied [config] block, and produces a value.
 * This keeps the arb reproducible and lets callers customize strategies or factories
 * per property test.
 *
 * Usage:
 * ```kotlin
 * check(Arb.some<MyType>(), Arb.some<OtherType>()) { a, b ->
 *     // property test assertions
 * }
 * ```
 *
 * @param T Type to generate.
 * @param config Configuration applied to each `Some` instance before generating a value.
 * @return An [Arb] producing random values of type [T].
 */
inline fun <reified T> Arb.Companion.some(
    crossinline config: SomeConfigBuilder.() -> Unit = {},
): Arb<T> = object : Arb<T>() {
    override fun edgecase(rs: RandomSource): Sample<T>? = null

    override fun sample(rs: RandomSource): Sample<T> {
        val someGenerator = someSetup {
            seed = rs.seed
            config()
        }

        return Sample(someGenerator<T>())
    }
}
