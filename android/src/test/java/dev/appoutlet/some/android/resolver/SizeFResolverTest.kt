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
class SizeFResolverTest {
    @Test
    fun `SizeFResolver generates SizeF values`() {
        val resolver = SizeFResolver(Random.Default)
        val result = resolver.resolve(typeOf<SizeF>(), testChain)
        assertTrue(result is SizeF)
    }

    @Test
    fun `SizeFResolver canResolve detects SizeF type`() {
        val resolver = SizeFResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<SizeF>()))
    }

    @Test
    fun `SizeFResolver rejects non-SizeF types`() {
        val resolver = SizeFResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
        assertFalse(resolver.canResolve(typeOf<Rect>()))
        assertFalse(resolver.canResolve(typeOf<RectF>()))
        assertFalse(resolver.canResolve(typeOf<Point>()))
        assertFalse(resolver.canResolve(typeOf<PointF>()))
        assertFalse(resolver.canResolve(typeOf<Size>()))
    }

    @Test
    fun `SizeFResolver generates sizes with positive dimensions`() {
        val resolver = SizeFResolver(Random.Default)
        repeat(20) {
            val result = resolver.resolve(typeOf<SizeF>(), testChain) as SizeF
            assertTrue("width should be positive", result.width > 0)
            assertTrue("height should be positive", result.height > 0)
        }
    }

    companion object {
        private val testChain = ResolverChain(emptyList(), NullableStrategy.NullOnCircularReference)
    }
}
