package dev.appoutlet.some.android.resolver

import android.os.Bundle
import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.core.ResolverChain
import org.junit.Assert.assertEquals
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
    fun `BundleResolver generates one entry for each supported type`() {
        val resolver = BundleResolver(Random.Default)

        repeat(20) {
            val result = resolver.resolve(typeOf<Bundle>(), testChain) as Bundle

            assertEquals(6, result.size())
            assertEquals(setOf("string", "int", "long", "float", "double", "boolean"), result.keySet())
            assertNotNull(result.getString("string"))
            assertTrue(result.getString("string")!!.isNotEmpty())
            assertTrue(result.getValue("string") is String)
            assertTrue(result.getValue("int") is Int)
            assertTrue(result.getValue("long") is Long)
            assertTrue(result.getValue("float") is Float)
            assertTrue(result.getValue("double") is Double)
            assertTrue(result.getValue("boolean") is Boolean)
        }
    }

    @Suppress("DEPRECATION")
    private fun Bundle.getValue(key: String): Any? = get(key)

    companion object {
        private val testChain = ResolverChain(emptyList(), NullableStrategy.NullOnCircularReference)
    }
}
