package dev.appoutlet.some.android.resolver.compose

import androidx.compose.ui.unit.IntSize
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
class IntSizeResolverTest {
    @Test
    fun `IntSizeResolver generates IntSize values`() {
        val resolver = IntSizeResolver(Random.Default)
        val result = resolver.resolve(typeOf<IntSize>(), testChain)
        assertTrue(result is IntSize)
    }

    @Test
    fun `IntSizeResolver generates positive dimensions`() {
        val resolver = IntSizeResolver(Random.Default)
        repeat(100) {
            val result = resolver.resolve(typeOf<IntSize>(), testChain) as IntSize
            assertTrue(result.width >= 0)
            assertTrue(result.height >= 0)
        }
    }

    @Test
    fun `IntSizeResolver canResolve detects IntSize type`() {
        val resolver = IntSizeResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<IntSize>()))
    }

    @Test
    fun `IntSizeResolver rejects non-IntSize types`() {
        val resolver = IntSizeResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<Int>()))
        assertFalse(resolver.canResolve(typeOf<Float>()))
    }

    companion object {
        private val testChain = ResolverChain(emptyList(), NullableStrategy.NullOnCircularReference)
    }
}
