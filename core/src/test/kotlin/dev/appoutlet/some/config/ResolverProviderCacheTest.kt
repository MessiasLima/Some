package dev.appoutlet.some.config

import dev.appoutlet.some.test.CountingResolverProvider
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class ResolverProviderCacheTest {
    @Test
    fun `providers are instantiated once per configuration`() {
        val config = SomeConfig()
        val instantiationsBefore = CountingResolverProvider.instantiations.get()
        val createCallsBefore = CountingResolverProvider.createCalls.get()

        config.buildResolvers()
        val instantiationsAfterFirstBuild = CountingResolverProvider.instantiations.get()
        config.buildResolvers()

        assertEquals(instantiationsBefore + 1, instantiationsAfterFirstBuild)
        assertEquals(instantiationsAfterFirstBuild, CountingResolverProvider.instantiations.get())
        assertEquals(createCallsBefore + 2, CountingResolverProvider.createCalls.get())
    }

    @Test
    fun `createResolvers runs on every build with the current arguments`() {
        val config = SomeConfig()
        val firstRandom = Random(1)
        val secondRandom = Random(2)
        val createCallsBefore = CountingResolverProvider.createCalls.get()

        config.buildResolvers(firstRandom)
        config.buildResolvers(secondRandom)

        assertEquals(createCallsBefore + 2, CountingResolverProvider.createCalls.get())
        val recordedRandoms = CountingResolverProvider.randoms.takeLast(2)
        assertSame(firstRandom, recordedRandoms[0])
        assertSame(secondRandom, recordedRandoms[1])
    }

    @Test
    fun `concurrent builds discover providers once`() {
        val config = SomeConfig()
        val instantiationsBefore = CountingResolverProvider.instantiations.get()
        val createCallsBefore = CountingResolverProvider.createCalls.get()
        val executor = Executors.newFixedThreadPool(4)

        try {
            val tasks = List(16) { index ->
                Callable { config.buildResolvers(Random(index)) }
            }
            executor.invokeAll(tasks).forEach { it.get() }
        } finally {
            executor.shutdownNow()
        }

        assertEquals(instantiationsBefore + 1, CountingResolverProvider.instantiations.get())
        assertEquals(createCallsBefore + 16, CountingResolverProvider.createCalls.get())
    }
}
