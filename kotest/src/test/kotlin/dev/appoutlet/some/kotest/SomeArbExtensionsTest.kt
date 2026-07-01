package dev.appoutlet.some.kotest

import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.boolean
import kotlin.test.Test
import kotlin.test.assertIs

class SomeArbExtensionsTest {
    @Test
    fun `Arb companion some extension generates values`() {
        val arb = Arb.some<String>()

        val sample = arb.sample(RandomSource.default())

        assertIs<String>(sample.value)
    }
}
