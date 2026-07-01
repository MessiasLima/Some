package dev.appoutlet.some.android.resolver

import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.util.Size
import android.util.SizeF
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
class RectResolverTest {
    @Test
    fun `RectResolver generates Rect values`() {
        val resolver = RectResolver(Random.Default)
        val result = resolver.resolve(typeOf<Rect>(), testChain)
        assertTrue(result is Rect)
    }

    @Test
    fun `RectResolver canResolve detects Rect type`() {
        val resolver = RectResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<Rect>()))
    }

    @Test
    fun `RectResolver rejects non-Rect types`() {
        val resolver = RectResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
        assertFalse(resolver.canResolve(typeOf<RectF>()))
        assertFalse(resolver.canResolve(typeOf<Point>()))
        assertFalse(resolver.canResolve(typeOf<PointF>()))
        assertFalse(resolver.canResolve(typeOf<Size>()))
        assertFalse(resolver.canResolve(typeOf<SizeF>()))
    }

    @Test
    fun `RectResolver generates rects with valid bounds`() {
        val resolver = RectResolver(Random.Default)
        repeat(20) {
            val result = resolver.resolve(typeOf<Rect>(), testChain) as Rect
            assertTrue("left should be less than right", result.left < result.right)
            assertTrue("top should be less than bottom", result.top < result.bottom)
        }
    }

    companion object {
        private val testChain = ResolverChain(emptyList(), NullableStrategy.NullOnCircularReference)
    }
}
