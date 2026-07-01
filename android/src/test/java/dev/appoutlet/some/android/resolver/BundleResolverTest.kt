package dev.appoutlet.some.android.resolver

import android.os.Bundle
import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.core.ResolverChain
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.random.Random
import kotlin.reflect.typeOf

@RunWith(RobolectricTestRunner::class)
class BundleResolverTest {
    @Test
    fun `BundleResolver canResolve detects Bundle type`() {
        val resolver = BundleResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<Bundle>()))
    }

    @Test
    fun `BundleResolver rejects non-Bundle types`() {
        val resolver = BundleResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
    }

    @Test
    fun `BundleResolver generates non-null Bundle`() {
        val resolver = BundleResolver(Random.Default)
        val result = resolver.resolve(typeOf<Bundle>(), testChain)
        assertNotNull(result)
        assertTrue(result is Bundle)
    }

    @Test
    fun `BundleResolver generates bundles with 0 to 5 entries`() {
        val resolver = BundleResolver(Random.Default)

        repeat(20) {
            val result = resolver.resolve(typeOf<Bundle>(), testChain) as Bundle
            assertTrue(
                "Expected 0 to 5 entries but got ${result.size()}",
                result.size() in MIN_ENTRY_COUNT..MAX_ENTRY_COUNT
            )
        }
    }

    @Test
    fun `BundleResolver keys are non-empty`() {
        val resolver = BundleResolver(Random.Default)

        repeat(20) {
            val result = resolver.resolve(typeOf<Bundle>(), testChain) as Bundle
            result.keySet().forEach { key ->
                assertTrue("Key should not be empty", key.isNotEmpty())
            }
        }
    }

    @Test
    fun `BundleResolver values are valid types`() {
        val resolver = BundleResolver(Random.Default)

        repeat(20) {
            val result = resolver.resolve(typeOf<Bundle>(), testChain) as Bundle
            result.keySet().forEach { key ->
                val value = result.getValue(key)
                assertNotNull(value)
                assertTrue(
                    "Unexpected value type: ${value!!.javaClass}",
                    value is String ||
                        value is Int ||
                        value is Long ||
                        value is Float ||
                        value is Double ||
                        value is Boolean
                )
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun Bundle.getValue(key: String): Any? = get(key)

    companion object {
        private const val MIN_ENTRY_COUNT = 0
        private const val MAX_ENTRY_COUNT = 5

        private val testChain = ResolverChain(emptyList(), NullableStrategy.NullOnCircularReference)
    }
}
