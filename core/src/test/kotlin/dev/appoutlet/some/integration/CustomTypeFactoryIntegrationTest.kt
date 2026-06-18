package dev.appoutlet.some.integration

import dev.appoutlet.some.some
import dev.appoutlet.some.someSetup
import kotlin.test.Test
import kotlin.test.assertEquals

class CustomTypeFactoryIntegrationTest {
    @Test
    fun `custom type factory overrides built-in resolver`() {
        val customSome = someSetup {
            factory(String::class) { "custom-value" }
        }

        data class Simple(val text: String)
        val result: Simple = customSome.some<Simple>()
        assertEquals("custom-value", result.text)
    }

    @Test
    fun `aggregated configs work with custom type factories`() {
        data class Product(val name: String, val price: Double)

        // Base config with custom type factory
        val baseSome = someSetup {
            factory(String::class) { "base-string" }
        }

        val result1: Product = baseSome()
        assertEquals("base-string", result1.name)

        // Aggregate with another custom type factory - should override in the aggregated config
        // This should NOT mutate the base config
        val result2: Product = baseSome {
            factory(String::class) { "overridden-string" }
        }
        assertEquals("overridden-string", result2.name)

        // Base config should NOT be mutated, so it will still use the original type factory
        val result3: Product = baseSome()
        assertEquals("base-string", result3.name)
    }
}
