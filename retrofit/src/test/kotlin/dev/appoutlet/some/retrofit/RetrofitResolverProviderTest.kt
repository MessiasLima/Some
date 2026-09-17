package dev.appoutlet.some.retrofit

import dev.appoutlet.some.config.DefaultStrategyProvider
import dev.appoutlet.some.retrofit.resolver.ResponseResolver
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class RetrofitResolverProviderTest {
    @Test
    fun `createResolvers returns the Retrofit resolver list`() {
        val resolvers = RetrofitResolverProvider().createResolvers(DefaultStrategyProvider(), Random.Default)

        assertEquals(listOf(ResponseResolver::class), resolvers.map { it::class })
    }
}
