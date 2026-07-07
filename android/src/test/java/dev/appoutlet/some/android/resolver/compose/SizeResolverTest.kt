package dev.appoutlet.some.android.resolver.compose

import androidx.compose.ui.geometry.Size
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
    fun `SizeResolver generates positive dimensions`() {
        val resolver = SizeResolver(Random.Default)
        repeat(100) {
            val result = resolver.resolve(typeOf<Size>(), testChain) as Size
            assertTrue(result.width >= 0f)
            assertTrue(result.height >= 0f)
        }
    }

    @Test
    fun `SizeResolver canResolve detects Size type`() {
        val resolver = SizeResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<Size>()))
    }

    @Test
    fun `SizeResolver rejects non-Size types`() {
        val resolver = SizeResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<Float>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
    }

    companion object {
        private val testChain = ResolverChain(emptyList(), NullableStrategy.NullOnCircularReference)
    }
}
