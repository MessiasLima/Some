package dev.appoutlet.some.integration

import dev.appoutlet.some.some
import dev.appoutlet.some.someSetup
import kotlin.test.Test
import kotlin.test.assertEquals

class CustomFactoryIntegrationTest {
    @Test
    fun `custom factory overrides built-in resolver`() {
        val customSome = someSetup {
            register(String::class) { "custom-value" }
        }
        
        data class Simple(val text: String)
        val result: Simple = customSome.some<Simple>()
        assertEquals("custom-value", result.text)
    }
    
    @Test
    fun `aggregated configs work with custom factories`() {
        data class Product(val name: String, val price: Double)
        
        // Base config with custom factory
        val baseSome = someSetup {
            register(String::class) { "base-string" }
        }
        
        val result1: Product = baseSome()
        assertEquals("base-string", result1.name)
        
        // Aggregate with another custom factory - should override in the aggregated config
        // This should NOT mutate the base config
        val result2: Product = baseSome {
            register(String::class) { "overridden-string" }
        }
        assertEquals("overridden-string", result2.name)
        
        // Base config should NOT be mutated, so it will still use the original factory
        val result3: Product = baseSome()
        assertEquals("base-string", result3.name)
    }
}
