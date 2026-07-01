package dev.appoutlet.some.kotest

import dev.appoutlet.some.config.SomeConfigBuilder
import dev.appoutlet.some.someSetup
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample

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
