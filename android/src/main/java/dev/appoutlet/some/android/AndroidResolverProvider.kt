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
import dev.appoutlet.some.android.resolver.compose.SpResolver
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverProvider
import dev.appoutlet.some.core.StrategyProvider
import kotlin.random.Random
import dev.appoutlet.some.android.resolver.compose.ColorResolver as ComposeColorResolver
import dev.appoutlet.some.android.resolver.compose.RectResolver as ComposeRectResolver
import dev.appoutlet.some.android.resolver.compose.SizeResolver as ComposeSizeResolver

/**
 * Contributes Android-specific resolvers discovered through `ServiceLoader`.
 *
 * The returned resolvers extend the shared `some-core` chain with Android framework and Compose type support.
 */
class AndroidResolverProvider : ResolverProvider {
    /**
     * Creates the Android resolver set using the shared [strategyProvider] and [random] source.
     *
     * Resolvers that depend on strategies receive [strategyProvider]; all others share the same [random]
     * instance so generated Android values stay consistent with the rest of the fixture session.
     */
    override fun createResolvers(
        strategyProvider: StrategyProvider,
        random: Random
    ): List<Resolver> {
        return listOf(
            BundleResolver(random),
            ColorResolver(random),
            PairResolver(random),
            PointFResolver(random),
            PointResolver(random),
            RectFResolver(random),
            RectResolver(random),
            SizeFResolver(random),
            SizeResolver(random),
            UriResolver(strategyProvider, random),
            UserHandleResolver(random),
            AnnotatedStringResolver(random),
            ComposeColorResolver(strategyProvider, random),
            DpResolver(random),
            IntOffsetResolver(random),
            IntSizeResolver(random),
            OffsetResolver(random),
            ComposeRectResolver(random),
            ComposeSizeResolver(random),
            SpResolver(random),
        )
    }
}
