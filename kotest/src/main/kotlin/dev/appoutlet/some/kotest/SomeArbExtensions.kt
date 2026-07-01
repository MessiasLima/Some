package dev.appoutlet.some.kotest

import dev.appoutlet.some.config.SomeConfigBuilder
import dev.appoutlet.some.someSetup
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample

/**
 * Creates a Kotest [Arb] that generates values of type [T] using `Some`.
 *
 * Each call to [some] creates one reusable `Some` generator, applies the supplied [config]
 * block once, and returns an arb that produces values from that generator.
 *
 * This avoids rebuilding `Some` for every property sample. Generated values follow the
 * configured `Some` seed when one is provided in [config].
 *
 * Usage:
 * ```kotlin
 * check(Arb.some<MyType>(), Arb.some<OtherType>()) { a, b ->
 *     // property test assertions
 * }
 * ```
 *
 * @param T Type to generate.
 * @param config Configuration applied once when the underlying `Some` generator is created.
 * @return An [Arb] producing random values of type [T].
 */
inline fun <reified T> Arb.Companion.some(
    crossinline config: SomeConfigBuilder.() -> Unit = {},
): Arb<T> {
    return object : Arb<T>() {
        private val some = someSetup { config() }

        override fun edgecase(rs: RandomSource): Sample<T>? = null

        override fun sample(rs: RandomSource) = Sample(some<T>())
    }
}
