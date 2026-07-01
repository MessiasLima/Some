package dev.appoutlet.some.kotest

import dev.appoutlet.some.config.NullableStrategy
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class SomeArbExtensionsTest {
    @Test
    fun `Arb companion some extension generates values`() {
        val arb = Arb.some<String>()

        val sample = arb.sample(RandomSource.default())

        assertIs<String>(sample.value)
    }

    @Test
    fun `Arb companion some extension generates data classes`() {
        val arb = Arb.some<TestData>()

        val sample = arb.sample(RandomSource.default())

        assertIs<TestData>(sample.value)
    }

    @Test
    fun `Arb companion some extension applies configuration`() {
        val arb = Arb.some<String?> {
            strategy(NullableStrategy.NeverNull)
        }

        val sample = arb.sample(RandomSource.default())

        assertNotNull(sample.value)
    }

    @Test
    fun `Arb companion some extension is reproducible for the same seed`() {
        val arb = Arb.some<String>()
        val seed = 12345L

        val sample1 = arb.sample(RandomSource.seeded(seed))
        val sample2 = arb.sample(RandomSource.seeded(seed))

        assertEquals(sample1.value, sample2.value)
    }

    private data class TestData(val name: String, val age: Int)
}
