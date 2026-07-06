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
class RectFResolverTest {
    @Test
    fun `RectFResolver generates RectF values`() {
        val resolver = RectFResolver(Random.Default)
        val result = resolver.resolve(typeOf<RectF>(), testChain)
        assertTrue(result is RectF)
    }

    @Test
    fun `RectFResolver canResolve detects RectF type`() {
        val resolver = RectFResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<RectF>()))
    }

    @Test
    fun `RectFResolver rejects non-RectF types`() {
        val resolver = RectFResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
        assertFalse(resolver.canResolve(typeOf<Rect>()))
        assertFalse(resolver.canResolve(typeOf<Point>()))
        assertFalse(resolver.canResolve(typeOf<PointF>()))
        assertFalse(resolver.canResolve(typeOf<Size>()))
        assertFalse(resolver.canResolve(typeOf<SizeF>()))
    }

    @Test
    fun `RectFResolver generates rects with valid bounds`() {
        val resolver = RectFResolver(Random.Default)
        repeat(20) {
            val result = resolver.resolve(typeOf<RectF>(), testChain) as RectF
            assertTrue("left should be less than right", result.left < result.right)
            assertTrue("top should be less than bottom", result.top < result.bottom)
        }
    }

    companion object {
        private val testChain = ResolverChain(emptyList(), NullableStrategy.NullOnCircularReference)
    }
}
