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
import dev.appoutlet.some.android.resolver.compose.DpResolver
import dev.appoutlet.some.android.resolver.compose.IntOffsetResolver
import dev.appoutlet.some.android.resolver.compose.IntSizeResolver
import dev.appoutlet.some.android.resolver.compose.OffsetResolver
import dev.appoutlet.some.android.resolver.compose.RectResolver as ComposeRectResolver
import dev.appoutlet.some.android.resolver.compose.SizeResolver as ComposeSizeResolver
import dev.appoutlet.some.android.resolver.compose.SpResolver
import dev.appoutlet.some.config.DefaultStrategyProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.random.Random

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
                DpResolver::class,
                SpResolver::class,
                OffsetResolver::class,
                ComposeSizeResolver::class,
                ComposeRectResolver::class,
                IntOffsetResolver::class,
                IntSizeResolver::class
            ),
            resolvers.map { it::class }
        )
    }
}
