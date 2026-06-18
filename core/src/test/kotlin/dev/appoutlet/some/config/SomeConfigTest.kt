package dev.appoutlet.some.config

import dev.appoutlet.some.core.ResolverChain
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals

class SomeConfigTest {
    @Test
    fun `SomeConfig with seed produces deterministic results`() {
        val config1 = SomeConfig(seed = 42L)
        val chain1 = ResolverChain(config1.buildResolvers(), config1[NullableStrategy::class])

        val config2 = SomeConfig(seed = 42L)
        val chain2 = ResolverChain(config2.buildResolvers(), config2[NullableStrategy::class])

        val result1 = chain1.resolve(typeOf<Int>())
        val result2 = chain2.resolve(typeOf<Int>())

        assertEquals(result1, result2)
    }

    @Test
    fun `SomeConfig toBuilder creates pre-populated builder`() {
        val config = SomeConfig(
            seed = 99L,
            strategies = mapOf(NullableStrategy::class to NullableStrategy.NeverNull),
        )
        val builder = config.toBuilder()
        val rebuilt = builder.build()

        assertEquals(99L, rebuilt.seed)
    }
}
