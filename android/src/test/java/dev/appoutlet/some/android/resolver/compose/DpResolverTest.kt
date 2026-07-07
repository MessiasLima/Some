package dev.appoutlet.some.android.resolver.compose

import androidx.compose.ui.unit.Dp
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
class DpResolverTest {
    @Test
    fun `DpResolver generates Dp values`() {
        val resolver = DpResolver(Random.Default)
        val result = resolver.resolve(typeOf<Dp>(), testChain)
        assertTrue(result is Dp)
    }

    @Test
    fun `DpResolver generates values within range`() {
        val resolver = DpResolver(Random.Default)
        repeat(100) {
            val result = resolver.resolve(typeOf<Dp>(), testChain) as Dp
            assertTrue(result.value in 0f..1000f)
        }
    }

    @Test
    fun `DpResolver canResolve detects Dp type`() {
        val resolver = DpResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<Dp>()))
    }

    @Test
    fun `DpResolver rejects non-Dp types`() {
        val resolver = DpResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<Float>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
    }

    companion object {
        private val testChain = ResolverChain(emptyList(), NullableStrategy.NullOnCircularReference)
    }
}
