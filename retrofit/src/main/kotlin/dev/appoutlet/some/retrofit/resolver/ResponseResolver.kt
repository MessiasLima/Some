package dev.appoutlet.some.retrofit.resolver

import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import retrofit2.Response
import kotlin.reflect.KType

/**
 * Resolves [Response] values by delegating their body type to the active resolver chain.
 */
class ResponseResolver : Resolver {
    override fun canResolve(type: KType): Boolean = type.classifier == Response::class

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val bodyType = requireNotNull(type.arguments.firstOrNull()?.type) {
            "Star projection not supported in Response"
        }

        return Response.success(chain.resolve(bodyType))
    }
}
