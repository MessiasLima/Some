package dev.appoutlet.some.android.resolver

import android.net.Uri
import dev.appoutlet.some.android.strategy.UriStrategy
import dev.appoutlet.some.config.DefaultStrategyProvider
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
class UriResolverTest {
    @Test
    fun `UriResolver canResolve detects Uri type`() {
        val resolver = UriResolver(DefaultStrategyProvider(), Random.Default)
        assertTrue(resolver.canResolve(typeOf<Uri>()))
    }

    @Test
    fun `UriResolver rejects non-Uri types`() {
        val resolver = UriResolver(DefaultStrategyProvider(), Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
    }

    @Test
    fun `UriStrategy Random generates URIs with allowed schemes`() {
        val resolver = UriResolver(
            DefaultStrategyProvider(mapOf(UriStrategy::class to UriStrategy.Random)),
            Random.Default
        )

        repeat(20) {
            val result = resolver.resolve(typeOf<Uri>(), testChain) as Uri
            assertTrue(
                "Expected scheme in [content, file, https] but got ${result.scheme}",
                result.scheme in setOf("content", "file", "https")
            )
            assertUriShape(result)
        }
    }

    @Test
    fun `UriStrategy Url generates only https URIs`() {
        val resolver = UriResolver(
            DefaultStrategyProvider(mapOf(UriStrategy::class to UriStrategy.Url)),
            Random.Default
        )

        repeat(10) {
            val result = resolver.resolve(typeOf<Uri>(), testChain) as Uri
            assertEquals("https", result.scheme)
            assertUriShape(result)
        }
    }

    @Test
    fun `UriStrategy Content generates only content URIs`() {
        val resolver = UriResolver(
            DefaultStrategyProvider(mapOf(UriStrategy::class to UriStrategy.Content)),
            Random.Default
        )

        repeat(10) {
            val result = resolver.resolve(typeOf<Uri>(), testChain) as Uri
            assertEquals("content", result.scheme)
            assertUriShape(result)
        }
    }

    @Test
    fun `UriStrategy File generates only file URIs`() {
        val resolver = UriResolver(
            DefaultStrategyProvider(mapOf(UriStrategy::class to UriStrategy.File)),
            Random.Default
        )

        repeat(10) {
            val result = resolver.resolve(typeOf<Uri>(), testChain) as Uri
            assertEquals("file", result.scheme)
            assertUriShape(result)
        }
    }

    @Test
    fun `Generated URIs are parseable by Uri parse`() {
        val resolver = UriResolver(DefaultStrategyProvider(), Random.Default)

        repeat(20) {
            val result = resolver.resolve(typeOf<Uri>(), testChain) as Uri
            val reparsed = Uri.parse(result.toString())
            assertNotNull(reparsed)
            assertEquals(result.scheme, reparsed.scheme)
        }
    }

    @Test
    fun `Default strategy is Random`() {
        val resolver = UriResolver(DefaultStrategyProvider(), Random.Default)
        val result = resolver.resolve(typeOf<Uri>(), testChain) as Uri
        assertTrue(
            "Expected scheme in [content, file, https] but got ${result.scheme}",
            result.scheme in setOf("content", "file", "https")
        )
        assertUriShape(result)
    }

    private fun assertUriShape(uri: Uri) {
        assertNotNull(uri.scheme)
        assertTrue("Host should not be empty", uri.host?.isNotEmpty() == true)
        assertTrue("Path should not be empty", uri.path?.isNotEmpty() == true)
    }

    companion object {
        private val testChain = ResolverChain(emptyList(), NullableStrategy.NullOnCircularReference)
    }
}
