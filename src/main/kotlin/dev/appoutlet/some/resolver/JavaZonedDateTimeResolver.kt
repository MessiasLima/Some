package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.TypeResolver
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

private const val START_EPOCH = 0L // 1970-01-01T00:00:00Z
private const val END_EPOCH = 4133894400L // 2101-01-01T00:00:00Z

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
        val epochSecond = random.nextLong(START_EPOCH, END_EPOCH)
        val zoneId = ZoneId.of(zoneIds[random.nextInt(zoneIds.size)])
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), zoneId)
    }
}
