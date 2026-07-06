package dev.appoutlet.some.android.resolver

import android.os.UserHandle
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Resolves [UserHandle] values when the runtime API level supports [UserHandle.getUserHandleForUid].
 *
 * @param random Random source used when generating a non-system application UID.
 */
class UserHandleResolver(
    private val random: Random
) : Resolver {
    override fun canResolve(type: KType): Boolean = type == typeOf<UserHandle>()

    override fun resolve(type: KType, chain: ResolverChain): Any? {
        return UserHandle.getUserHandleForUid(random.nextInt())
    }
}
