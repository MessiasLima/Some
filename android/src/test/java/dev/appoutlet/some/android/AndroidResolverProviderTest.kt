package dev.appoutlet.some.android

import dev.appoutlet.some.android.resolver.BundleResolver
import dev.appoutlet.some.android.resolver.ColorResolver
import dev.appoutlet.some.android.resolver.PairResolver
import dev.appoutlet.some.android.resolver.PointFResolver
import dev.appoutlet.some.android.resolver.PointResolver
import dev.appoutlet.some.android.resolver.RectFResolver
import dev.appoutlet.some.android.resolver.RectResolver
import dev.appoutlet.some.android.resolver.SizeFResolver
import dev.appoutlet.some.android.resolver.SizeResolver
import dev.appoutlet.some.android.resolver.UriResolver
import dev.appoutlet.some.android.resolver.UserHandleResolver
import dev.appoutlet.some.android.resolver.compose.AnnotatedStringResolver
import dev.appoutlet.some.config.DefaultStrategyProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.random.Random
import dev.appoutlet.some.android.resolver.compose.ColorResolver as ComposeColorResolver

class AndroidResolverProviderTest {
    @Test
    fun `createResolvers returns the full Android resolver list`() {
        val resolvers = AndroidResolverProvider().createResolvers(DefaultStrategyProvider(), Random.Default)

        assertEquals(
            listOf(
                UriResolver::class,
                BundleResolver::class,
                RectResolver::class,
                RectFResolver::class,
                PointResolver::class,
                PointFResolver::class,
                SizeResolver::class,
                SizeFResolver::class,
                PairResolver::class,
                UserHandleResolver::class,
                ColorResolver::class,
                AnnotatedStringResolver::class,
                ComposeColorResolver::class,
            ),
            resolvers.map { it::class }
        )
    }
}
