package dev.appoutlet.some.android.resolver

import android.graphics.Color
import android.os.UserHandle
import android.util.Pair
import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.core.ResolverChain
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.random.Random
import kotlin.reflect.typeOf

@RunWith(RobolectricTestRunner::class)
class ColorResolverTest {
    @Test
    @Config(sdk = [26])
    fun `ColorResolver generates Color values`() {
        val resolver = ColorResolver(Random.Default)
        val result = resolver.resolve(typeOf<Color>(), testChain)
        assertTrue(result is Color)
    }

    @Test
    fun `ColorResolver canResolve detects Color type`() {
        val resolver = ColorResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<Color>()))
    }

    @Test
    fun `ColorResolver rejects non-Color types`() {
        val resolver = ColorResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
        assertFalse(resolver.canResolve(typeOf<UserHandle>()))
        assertFalse(resolver.canResolve(typeOf<Pair<String, Int>>()))
    }

    companion object {
        private val testChain = ResolverChain(emptyList(), NullableStrategy.NullOnCircularReference)
    }
}
