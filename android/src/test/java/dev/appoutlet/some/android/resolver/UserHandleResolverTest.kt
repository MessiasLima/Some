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
class UserHandleResolverTest {
    @Test
    @Config(sdk = [26])
    fun `UserHandleResolver generates UserHandle values when API level is supported`() {
        val resolver = UserHandleResolver(Random.Default)
        val result = resolver.resolve(typeOf<UserHandle>(), testChain)
        assertTrue(result is UserHandle)
    }

    @Test
    fun `UserHandleResolver canResolve detects UserHandle type`() {
        val resolver = UserHandleResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<UserHandle>()))
    }

    @Test
    fun `UserHandleResolver rejects non-UserHandle types`() {
        val resolver = UserHandleResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
        assertFalse(resolver.canResolve(typeOf<Color>()))
        assertFalse(resolver.canResolve(typeOf<Pair<String, Int>>()))
    }

    companion object {
        private val testChain = ResolverChain(emptyList(), NullableStrategy.NullOnCircularReference)
    }
}
