package dev.appoutlet.some.android.strategy

import dev.appoutlet.some.config.Strategy

/**
 * Strategy for generating [android.net.Uri] values.
 *
 * Available strategies:
 * - [Random] – generates URIs with any scheme from `[content, file, https]` (default)
 * - [Content] – generates only `content://...` URIs
 * - [File] – generates only `file://...` URIs
 * - [Url] – generates only `https://...` URIs
 *
 * Example usage:
 * ```kotlin
 * someSetup {
 *     strategy(UriStrategy.Url)
 * }
 * ```
 */
sealed interface UriStrategy : Strategy {
    override val key get() = UriStrategy::class

    /**
     * Generates URIs with a random scheme from `[content, file, https]`.
     */
    data object Random : UriStrategy

    /**
     * Generates only `content://...` URIs.
     */
    data object Content : UriStrategy

    /**
     * Generates only `file://...` URIs.
     */
    data object File : UriStrategy

    /**
     * Generates only `https://...` URIs.
     */
    data object Url : UriStrategy

    companion object {
        /**
         * The default URI strategy.
         */
        val default: UriStrategy get() = Random
    }
}
