package dev.appoutlet.some.android.test

import dev.appoutlet.some.core.ResolverChain

/**
 * An empty resolver chain shared across Android resolver unit tests.
 */
val emptyTestChain: ResolverChain by lazy { ResolverChain(emptyList()) }
