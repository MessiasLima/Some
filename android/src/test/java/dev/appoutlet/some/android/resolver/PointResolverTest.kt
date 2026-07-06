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
class PointResolverTest {
    @Test
    fun `PointResolver generates Point values`() {
        val resolver = PointResolver(Random.Default)
        val result = resolver.resolve(typeOf<Point>(), testChain)
        assertTrue(result is Point)
    }

    @Test
    fun `PointResolver canResolve detects Point type`() {
        val resolver = PointResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<Point>()))
    }

    @Test
    fun `PointResolver rejects non-Point types`() {
        val resolver = PointResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
        assertFalse(resolver.canResolve(typeOf<Rect>()))
        assertFalse(resolver.canResolve(typeOf<RectF>()))
        assertFalse(resolver.canResolve(typeOf<PointF>()))
        assertFalse(resolver.canResolve(typeOf<Size>()))
        assertFalse(resolver.canResolve(typeOf<SizeF>()))
    }

    companion object {
        private val testChain = ResolverChain(emptyList(), NullableStrategy.NullOnCircularReference)
    }
}
