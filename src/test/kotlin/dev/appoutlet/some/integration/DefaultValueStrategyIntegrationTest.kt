package dev.appoutlet.some.integration

import dev.appoutlet.some.config.DefaultValueStrategy
import dev.appoutlet.some.some
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class DefaultValueStrategyIntegrationTest {

    data class OptionalData(
        val required: String,
        val optional: String = "default value"
    )

    @Test
    fun `should use default value when strategy is UseDefault`() {
        val result: OptionalData = some {
            strategy(DefaultValueStrategy.UseDefault)
        }

        assertEquals("default value", result.optional)
    }

    @Test
    fun `should use default value by default`() {
        val result: OptionalData = some()

        assertEquals("default value", result.optional)
    }

    @Test
    fun `should generate value when strategy is Generate`() {
        val result: OptionalData = some {
            strategy(DefaultValueStrategy.Generate)
        }

        assertNotEquals("default value", result.optional)
    }

    @Test
    fun `property factory should take precedence over UseDefault`() {
        val result: OptionalData = some {
            strategy(DefaultValueStrategy.UseDefault)
            property(OptionalData::optional) { "custom value" }
        }

        assertEquals("custom value", result.optional)
    }

    @Test
    fun `property factory should take precedence over Generate`() {
        val result: OptionalData = some {
            strategy(DefaultValueStrategy.Generate)
            property(OptionalData::optional) { "custom value" }
        }

        assertEquals("custom value", result.optional)
    }

    @Test
    fun `non-optional parameters should still be resolved`() {
        val result: OptionalData = some {
            strategy(DefaultValueStrategy.Generate)
        }

        assertNotEquals("", result.required)
    }
}
