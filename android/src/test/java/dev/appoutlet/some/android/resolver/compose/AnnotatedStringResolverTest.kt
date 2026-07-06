package dev.appoutlet.some.android.resolver.compose

import androidx.compose.ui.text.AnnotatedString
import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.core.ResolverChain
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random
import kotlin.reflect.typeOf

class AnnotatedStringResolverTest {
    @Test
    fun `AnnotatedStringResolver generates AnnotatedString values`() {
        val resolver = AnnotatedStringResolver(Random.Default)
        val result = resolver.resolve(typeOf<AnnotatedString>(), testChain)
        assertTrue(result is AnnotatedString)
    }

    @Test
    fun `AnnotatedStringResolver generates non-blank text`() {
        val resolver = AnnotatedStringResolver(Random.Default)

        repeat(20) {
            val result = resolver.resolve(typeOf<AnnotatedString>(), testChain) as AnnotatedString
            assertTrue("Generated AnnotatedString text should not be blank", result.text.isNotBlank())
        }
    }

    @Test
    fun `AnnotatedStringResolver canResolve detects AnnotatedString type`() {
        val resolver = AnnotatedStringResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<AnnotatedString>()))
    }

    @Test
    fun `AnnotatedStringResolver rejects non-AnnotatedString types`() {
        val resolver = AnnotatedStringResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
    }

    companion object {
        private val testChain = ResolverChain(emptyList(), NullableStrategy.NullOnCircularReference)
    }
}
