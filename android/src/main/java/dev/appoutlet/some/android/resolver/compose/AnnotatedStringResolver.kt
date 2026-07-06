package dev.appoutlet.some.android.resolver.compose

import androidx.compose.ui.text.AnnotatedString
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

private const val MIN_TEXT_LENGTH = 1
private const val MAX_TEXT_LENGTH = 20

/**
 * Resolves [AnnotatedString] types with randomized non-blank text content.
 *
 * @param random Random source used when generating text.
 */
class AnnotatedStringResolver(
    private val random: Random
) : Resolver {
    override fun canResolve(type: KType): Boolean = type == typeOf<AnnotatedString>()

    override fun resolve(type: KType, chain: ResolverChain): Any {
        return AnnotatedString(generateText())
    }

    private fun generateText(): String {
        val length = random.nextInt(MIN_TEXT_LENGTH, MAX_TEXT_LENGTH + 1)
        return List(length) { ('a'..'z').random(random) }.joinToString("")
    }
}
