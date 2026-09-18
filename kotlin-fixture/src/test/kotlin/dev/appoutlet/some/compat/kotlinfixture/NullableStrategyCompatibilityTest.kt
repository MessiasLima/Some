package dev.appoutlet.some.compat.kotlinfixture

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NullableStrategyCompatibilityTest {

    @Test
    fun `NeverNullStrategy produces non-null values for nullable types`() {
        val fixture = kotlinFixture {
            nullabilityStrategy = NullabilityStrategy.NeverNullStrategy
        }

        repeat(20) {
            val value: String? = fixture()
            assertNotNull(value)
        }
    }

    @Test
    fun `AlwaysNullStrategy produces null values for nullable types`() {
        val fixture = kotlinFixture {
            nullabilityStrategy = NullabilityStrategy.AlwaysNullStrategy
        }

        repeat(20) {
            val value: String? = fixture()
            assertNull(value)
        }
    }

    @Test
    fun `RandomlyNullStrategy produces mix of null and non-null values`() {
        val fixture = kotlinFixture {
            nullabilityStrategy = NullabilityStrategy.RandomlyNullStrategy(0.5f)
        }

        val results = List(50) { fixture<String?>() }
        val nullCount = results.count { it == null }
        val nonNullCount = results.count { it != null }

        assertTrue(nullCount > 0, "Expected at least one null value")
        assertTrue(nonNullCount > 0, "Expected at least one non-null value")
    }
}
