package dev.appoutlet.some.integration

import dev.appoutlet.some.some
import dev.appoutlet.some.someSetup
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
    fun `reified type factory applies only to the exact generic type`() {
        val customSome = someSetup {
            factory<List<Int>> { listOf(1, 2, 3) }
        }

        assertEquals(listOf(1, 2, 3), customSome.some<List<Int>>())

        val strings: List<String> = customSome.some()
        assertTrue(strings.all { it is String })
    }

    @Test
    fun `type factory applies to a nullable request through the non-null fallback`() {
        val customSome = someSetup {
            factory<List<Number>> { listOf<Number>(1, 2) }
        }

        assertEquals(listOf<Number>(1, 2), customSome.some<List<Number>?>())
    }

    @Test
    fun `type factory takes precedence over a class factory`() {
        val customSome = someSetup {
            factory(List::class) { listOf("class-factory") }
            factory<List<Int>> { listOf(7) }
        }

        assertEquals(listOf(7), customSome.some<List<Int>>())
        assertEquals(listOf("class-factory"), customSome.some<List<String>>())
    }

    @Test
    fun `per-call configuration can override a type factory`() {
        val baseSome = someSetup {
            factory<List<Int>> { listOf(1) }
        }

        val overridden = baseSome.invoke<List<Int>> {
            factory<List<Int>> { listOf(2) }
        }

        assertEquals(listOf(2), overridden)
        assertEquals(listOf(1), baseSome.some<List<Int>>())
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
