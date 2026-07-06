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
class SizeResolverTest {
    @Test
    fun `SizeResolver generates Size values`() {
        val resolver = SizeResolver(Random.Default)
        val result = resolver.resolve(typeOf<Size>(), testChain)
        assertTrue(result is Size)
    }

    @Test
    fun `SizeResolver canResolve detects Size type`() {
        val resolver = SizeResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<Size>()))
    }

    @Test
    fun `SizeResolver rejects non-Size types`() {
        val resolver = SizeResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
        assertFalse(resolver.canResolve(typeOf<Rect>()))
        assertFalse(resolver.canResolve(typeOf<RectF>()))
        assertFalse(resolver.canResolve(typeOf<Point>()))
        assertFalse(resolver.canResolve(typeOf<PointF>()))
        assertFalse(resolver.canResolve(typeOf<SizeF>()))
    }

    @Test
    fun `SizeResolver generates sizes with positive dimensions`() {
        val resolver = SizeResolver(Random.Default)
        repeat(20) {
            val result = resolver.resolve(typeOf<Size>(), testChain) as Size
            assertTrue("width should be positive", result.width > 0)
            assertTrue("height should be positive", result.height > 0)
        }
    }

    companion object {
        private val testChain = ResolverChain(emptyList(), NullableStrategy.NullOnCircularReference)
    }
}
