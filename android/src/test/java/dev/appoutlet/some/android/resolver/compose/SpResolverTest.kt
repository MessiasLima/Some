package dev.appoutlet.some.android.resolver.compose

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
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
class SpResolverTest {
    @Test
    fun `SpResolver generates TextUnit values`() {
        val resolver = SpResolver(Random.Default)
        val result = resolver.resolve(typeOf<TextUnit>(), testChain)
        assertTrue(result is TextUnit)
        assertTrue((result as TextUnit).isSp)
    }

    @Test
    fun `SpResolver generates values within range`() {
        val resolver = SpResolver(Random.Default)
        repeat(100) {
            val result = resolver.resolve(typeOf<TextUnit>(), testChain) as TextUnit
            assertTrue(result.value in 0f..100f)
        }
    }

    @Test
    fun `SpResolver canResolve detects TextUnit type`() {
        val resolver = SpResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<TextUnit>()))
    }

    @Test
    fun `SpResolver rejects non-TextUnit types`() {
        val resolver = SpResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<Float>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
    }

    companion object {
        private val testChain = ResolverChain(emptyList(), NullableStrategy.NullOnCircularReference)
    }
}
