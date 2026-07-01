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
class PointFResolverTest {
    @Test
    fun `PointFResolver generates PointF values`() {
        val resolver = PointFResolver(Random.Default)
        val result = resolver.resolve(typeOf<PointF>(), testChain)
        assertTrue(result is PointF)
    }

    @Test
    fun `PointFResolver canResolve detects PointF type`() {
        val resolver = PointFResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<PointF>()))
    }

    @Test
    fun `PointFResolver rejects non-PointF types`() {
        val resolver = PointFResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
        assertFalse(resolver.canResolve(typeOf<Rect>()))
        assertFalse(resolver.canResolve(typeOf<RectF>()))
        assertFalse(resolver.canResolve(typeOf<Point>()))
        assertFalse(resolver.canResolve(typeOf<Size>()))
        assertFalse(resolver.canResolve(typeOf<SizeF>()))
    }

    companion object {
        private val testChain = ResolverChain(emptyList(), NullableStrategy.NullOnCircularReference)
    }
}
