package dev.appoutlet.some.android.resolver.compose

import androidx.compose.ui.geometry.Offset
import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.core.ResolverChain
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.random.Random
import kotlin.reflect.typeOf

@RunWith(RobolectricTestRunner::class)
class OffsetResolverTest {
    @Test
    fun `OffsetResolver generates Offset values`() {
        val resolver = OffsetResolver(Random.Default)
        val result = resolver.resolve(typeOf<Offset>(), testChain)
        assertTrue(result is Offset)
    }

    @Test
    fun `OffsetResolver canResolve detects Offset type`() {
        val resolver = OffsetResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<Offset>()))
    }

    @Test
    fun `OffsetResolver rejects non-Offset types`() {
        val resolver = OffsetResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<Float>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
    }

    companion object {
        private val testChain = ResolverChain(emptyList(), NullableStrategy.NullOnCircularReference)
    }
}
