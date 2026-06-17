package dev.appoutlet.some.config

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DefaultStrategyProviderTest {
    @Test
    fun `DefaultStrategyProvider returns registered strategy`() {
        val provider = DefaultStrategyProvider(mapOf(NullableStrategy::class to NullableStrategy.AlwaysNull))

        assertEquals(NullableStrategy.AlwaysNull, provider[NullableStrategy::class])
    }

    @Test
    fun `DefaultStrategyProvider returns null for unregistered strategy`() {
        val provider = DefaultStrategyProvider()

        assertNull(provider[NullableStrategy::class])
    }

    @Test
    fun `DefaultStrategyProvider returns strategies from the provided map`() {
        val provider = DefaultStrategyProvider(
            mapOf(
                NullableStrategy::class to NullableStrategy.default,
                StringStrategy::class to StringStrategy.default,
                CollectionStrategy::class to CollectionStrategy.default,
                DefaultValueStrategy::class to DefaultValueStrategy.default,
            )
        )

        assertEquals(NullableStrategy.default, provider[NullableStrategy::class])
        assertEquals(StringStrategy.default, provider[StringStrategy::class])
        assertEquals(CollectionStrategy.default, provider[CollectionStrategy::class])
        assertEquals(DefaultValueStrategy.default, provider[DefaultValueStrategy::class])
    }
}
