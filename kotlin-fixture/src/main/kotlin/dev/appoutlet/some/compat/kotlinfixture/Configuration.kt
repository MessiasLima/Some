package dev.appoutlet.some.compat.kotlinfixture

import dev.appoutlet.some.config.CollectionStrategy
import dev.appoutlet.some.config.SomeConfigBuilder
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.defaultResolvers
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberProperties

/**
 * Immutable configuration for compatibility fixture generation.
 */
data class Configuration(
    val repeatCount: () -> Int = defaultRepeatCount,
    val propertiesRepeatCount: Map<KClass<*>, Map<String, () -> Int>> = emptyMap<KClass<*>, Map<String, () -> Int>>(),
    val properties: Map<KClass<*>, Map<String, GeneratorFun>> = emptyMap(),
    val factories: Map<KType, GeneratorFun> = emptyMap(),
    val subTypes: Map<KClass<*>, KClass<*>> = emptyMap(),
    val random: Random = Random.Default,
    val resolvers: List<Resolver> = defaultResolvers,
    val strategies: Map<KClass<*>, Any> = emptyMap(),
) {
    companion object {
        private val defaultRepeatCount: () -> Int = { 5 }
    }
}

 fun SomeConfigBuilder.parseConfiguration(configuration: Configuration, fixture: Fixture) {
    strategy(CollectionStrategy(configuration.repeatCount()))
    setupProperties(configuration, fixture)
}

fun SomeConfigBuilder.setupProperties(configuration: Configuration, fixture: Fixture) {
    configuration.properties.forEach { (klass, propertyGenerator) ->
        propertyGenerator.forEach { (name, function) ->
            val generator = object : Generator<Any?> {
                override val random = configuration.random
                override val fixture = fixture
            }

            val kProperty  = klass.declaredMemberProperties.find { it.name == name }

            property(kProperty) {
                function(generator)
            }
        }
    }
}