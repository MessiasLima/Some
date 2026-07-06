package dev.appoutlet.some.android.resolver

import android.graphics.Color
import android.os.UserHandle
import android.util.Pair
import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@RunWith(RobolectricTestRunner::class)
class PairResolverTest {
    @Test
    fun `PairResolver generates Pair values by delegating both generic types to the chain`() {
        val resolver = PairResolver(Random.Default)
        val resolvedTypes = mutableListOf<KType>()
        val chain = ResolverChain(
            listOf(
                TrackingResolver(typeOf<String>(), "first-value", resolvedTypes),
                TrackingResolver(typeOf<Int>(), 42, resolvedTypes)
            ),
            NullableStrategy.NullOnCircularReference
        )

        val result = resolver.resolve(typeOf<Pair<String, Int>>(), chain) as Pair<*, *>

        assertEquals("first-value", result.first)
        assertEquals(42, result.second)
        assertEquals(listOf(typeOf<String>(), typeOf<Int>()), resolvedTypes)
    }

    @Test
    fun `PairResolver canResolve detects Pair type`() {
        val resolver = PairResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<Pair<String, Int>>()))
    }

    @Test
    fun `PairResolver rejects non-Pair types`() {
        val resolver = PairResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
        assertFalse(resolver.canResolve(typeOf<Color>()))
        assertFalse(resolver.canResolve(typeOf<UserHandle>()))
        assertFalse(resolver.canResolve(typeOf<kotlin.Pair<String, Int>>()))
    }

    private class TrackingResolver(
        private val supportedType: KType,
        private val value: Any,
        private val resolvedTypes: MutableList<KType>
    ) : Resolver {
        override fun canResolve(type: KType): Boolean = type == supportedType

        override fun resolve(type: KType, chain: ResolverChain): Any {
            resolvedTypes += type
            return value
        }
    }
}
