package dev.appoutlet.some.resolver.zoneddatetime

import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.TypeResolver
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Resolves [ZonedDateTime] instances.
 *
 * Generated values fall within the range 1970-01-01 to 2100-12-31.
 * Each instance uses a randomly selected [ZoneId] from the available set on the JVM.
 *
 * @param random The random source used for generation.
 */
class JavaZonedDateTimeResolver(private val random: Random) : TypeResolver {
    private val zoneIds by lazy { ZoneId.getAvailableZoneIds().toList() }

    override fun canResolve(type: KType): Boolean {
        return type == typeOf<ZonedDateTime>()
    }

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val epochSecond = random.nextLong(Instant.MIN.epochSecond, Instant.MAX.epochSecond)
        val zoneId = zoneIds[random.nextInt(zoneIds.size)]
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), ZoneId.of(zoneId))
    }
}
