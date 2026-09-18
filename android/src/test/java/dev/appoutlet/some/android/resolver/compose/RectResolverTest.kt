package dev.appoutlet.some.android.resolver.compose

import androidx.compose.ui.geometry.Rect
import dev.appoutlet.some.android.test.emptyTestChain
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.random.Random
import kotlin.reflect.typeOf

@RunWith(RobolectricTestRunner::class)
class RectResolverTest {
    @Test
    fun `RectResolver generates Rect values`() {
        val resolver = RectResolver(Random.Default)
        val result = resolver.resolve(typeOf<Rect>(), emptyTestChain)
        assertTrue(result is Rect)
    }

    @Test
    fun `RectResolver generates valid bounds`() {
        val resolver = RectResolver(Random.Default)
        repeat(100) {
            val result = resolver.resolve(typeOf<Rect>(), emptyTestChain) as Rect
            assertTrue(result.left < result.right)
            assertTrue(result.top < result.bottom)
        }
    }

    @Test
    fun `RectResolver canResolve detects Rect type`() {
        val resolver = RectResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<Rect>()))
    }

    @Test
    fun `RectResolver rejects non-Rect types`() {
        val resolver = RectResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<Float>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
    }
}
