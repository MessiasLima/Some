package dev.appoutlet.some.config

import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.Strategy
import dev.appoutlet.some.core.StrategyProvider
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.TypeResolverProvider
import dev.appoutlet.some.logging.logger
import dev.appoutlet.some.resolver.bignumber.BigDecimalResolver
import dev.appoutlet.some.resolver.bignumber.BigIntegerResolver
import dev.appoutlet.some.resolver.collection.ArrayResolver
import dev.appoutlet.some.resolver.collection.ListResolver
import dev.appoutlet.some.resolver.collection.MapResolver
import dev.appoutlet.some.resolver.collection.SetResolver
import dev.appoutlet.some.resolver.custom.CustomTypeFactoryResolver
import dev.appoutlet.some.resolver.duration.JavaDurationResolver
import dev.appoutlet.some.resolver.duration.KotlinDurationResolver
import dev.appoutlet.some.resolver.enm.EnumResolver
import dev.appoutlet.some.resolver.instant.JavaInstantResolver
import dev.appoutlet.some.resolver.instant.KotlinInstantResolver
import dev.appoutlet.some.resolver.klass.ClassResolver
import dev.appoutlet.some.resolver.localdate.LocalDateResolver
import dev.appoutlet.some.resolver.localdatetime.LocalDateTimeResolver
import dev.appoutlet.some.resolver.nullable.NullableResolver
import dev.appoutlet.some.resolver.nullable.OptionalResolver
import dev.appoutlet.some.resolver.obj.ObjectResolver
import dev.appoutlet.some.resolver.primitive.BooleanResolver
import dev.appoutlet.some.resolver.primitive.ByteResolver
import dev.appoutlet.some.resolver.primitive.CharResolver
import dev.appoutlet.some.resolver.primitive.DoubleResolver
import dev.appoutlet.some.resolver.primitive.FloatResolver
import dev.appoutlet.some.resolver.primitive.IntResolver
import dev.appoutlet.some.resolver.primitive.LongResolver
import dev.appoutlet.some.resolver.primitive.NumberResolver
import dev.appoutlet.some.resolver.primitive.ShortResolver
import dev.appoutlet.some.resolver.sealed.SealedClassResolver
import dev.appoutlet.some.resolver.string.StringResolver
import dev.appoutlet.some.resolver.uuid.JavaUuidResolver
import dev.appoutlet.some.resolver.uuid.KotlinUuidResolver
import dev.appoutlet.some.resolver.valueclass.ValueClassResolver
import dev.appoutlet.some.resolver.zoneddatetime.JavaZonedDateTimeResolver
import java.util.ServiceLoader
import kotlin.random.Random
import kotlin.reflect.KClass

data class SomeConfig(
    val strategies: Map<KClass<out Strategy>, Strategy> = emptyMap(),
    val seed: Long? = null,
    val typeFactories: Map<KClass<*>, FixtureContext.() -> Any?> = emptyMap(),
    val propertyFactories: Map<Pair<KClass<*>, String>, FixtureContext.() -> Any?> = emptyMap(),
) : StrategyProvider {
    private val strategyProvider: StrategyProvider = DefaultStrategyProvider(strategies)
    private val logger by logger()

    override fun <T : Strategy> get(key: KClass<T>): T? = strategyProvider[key]

    fun toBuilder(): SomeConfigBuilder = SomeConfigBuilder().apply {
        populateStrategies(this@SomeConfig.strategies)
        seed = this@SomeConfig.seed
        populateTypeFactories(this@SomeConfig.typeFactories)
        populatePropertyFactories(this@SomeConfig.propertyFactories)
    }

    fun buildResolvers(random: Random = buildRandom()): List<TypeResolver> {
        val builtInResolvers = listOf(
            ObjectResolver(),
            EnumResolver(random),
            SealedClassResolver(random),
            ValueClassResolver(),
            StringResolver(strategyProvider, random),
            IntResolver(random),
            LongResolver(random),
            DoubleResolver(random),
            FloatResolver(random),
            BooleanResolver(random),
            CharResolver(random),
            ByteResolver(random),
            ShortResolver(random),
            NumberResolver(random),
            KotlinUuidResolver(),
            KotlinInstantResolver(random),
            KotlinDurationResolver(random),
            JavaUuidResolver(),
            JavaInstantResolver(random),
            JavaDurationResolver(random),
            JavaZonedDateTimeResolver(random),
            OptionalResolver(strategyProvider, random),
            BigDecimalResolver(random),
            BigIntegerResolver(random),
            LocalDateResolver(random),
            LocalDateTimeResolver(random),
            ListResolver(strategyProvider, random),
            SetResolver(strategyProvider, random),
            MapResolver(strategyProvider, random),
            ArrayResolver(strategyProvider, random),
        )

        val discoveredResolvers = discoverResolvers(strategyProvider, random)

        return listOf(
            CustomTypeFactoryResolver(strategyProvider, typeFactories, random),
            NullableResolver(strategyProvider, random),
        ) + discoveredResolvers + builtInResolvers + ClassResolver(strategyProvider, propertyFactories, random)
    }

    @Suppress("TooGenericExceptionCaught")
    private fun discoverResolvers(
        strategyProvider: StrategyProvider,
        random: Random,
    ): List<TypeResolver> {
        val loader = ServiceLoader.load(TypeResolverProvider::class.java)
        val providers = mutableListOf<TypeResolverProvider>()
        val iterator = loader.iterator()

        while (iterator.hasNext()) {
            try {
                providers.add(iterator.next())
            } catch (e: Throwable) {
                logger.w(e) { "Failed to load a TypeResolverProvider implementation" }
            }
        }

        return providers.flatMap { provider ->
            try {
                provider.createResolvers(strategyProvider, random)
            } catch (e: Throwable) {
                logger.w(e) { "Failed to instantiate resolver provider: ${provider::class.simpleName}" }
                emptyList()
            }
        }
    }

    fun buildRandom(): Random = seed?.let { Random(it) } ?: Random.Default
}
