package dev.appoutlet.some.android.resolver.compose

import androidx.compose.ui.unit.IntOffset
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
class IntOffsetResolverTest {
    @Test
    fun `IntOffsetResolver generates IntOffset values`() {
        val resolver = IntOffsetResolver(Random.Default)
        val result = resolver.resolve(typeOf<IntOffset>(), testChain)
        assertTrue(result is IntOffset)
    }

    @Test
    fun `IntOffsetResolver canResolve detects IntOffset type`() {
        val resolver = IntOffsetResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<IntOffset>()))
    }

    @Test
    fun `IntOffsetResolver rejects non-IntOffset types`() {
        val resolver = IntOffsetResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<Int>()))
        assertFalse(resolver.canResolve(typeOf<Float>()))
    }

    companion object {
        private val testChain = ResolverChain(emptyList(), NullableStrategy.NullOnCircularReference)
    }
}
