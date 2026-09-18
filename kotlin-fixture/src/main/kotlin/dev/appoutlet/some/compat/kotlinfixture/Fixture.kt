package dev.appoutlet.some.compat.kotlinfixture

import dev.appoutlet.some.Some
import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.core.ResolverChain
import kotlin.reflect.KType
import kotlin.reflect.typeOf

private const val MAX_FILTER_ATTEMPTS = 10000

/**
 * Main compatibility generator instance.
 */
class Fixture internal constructor(
    val configuration: Configuration
) {
    @PublishedApi
    internal val someInstance: Some = buildSomeInstance(configuration)

    /**
     * Generates a fixture value of type [T].
     *
     * @param range Iterable to select a value from when non-empty.
     * @param configuration Optional per-call configuration overrides.
     */
    inline operator fun <reified T : Any?> invoke(
        range: Iterable<T> = emptyList(),
        noinline configuration: (ConfigurationBuilder.() -> Unit)? = null
    ): T {
        val targetFixture = if (configuration != null) new(configuration) else this
        @Suppress("UNCHECKED_CAST")
        return targetFixture.generateValue(typeOf<T>(), range) as T
    }

    /**
     * Generates a sequence of fixture values of type [T].
     *
     * @param sequenceStrategy Strategy controlling sequence bounding.
     * @param configuration Optional per-call configuration overrides.
     */
    inline fun <reified T : Any?> asSequence(
        sequenceStrategy: SequenceStrategy = SequenceStrategy.Unbounded,
        noinline configuration: (ConfigurationBuilder.() -> Unit)? = null
    ): Sequence<T> {
        val targetFixture = if (configuration != null) new(configuration) else this
        @Suppress("UNCHECKED_CAST")
        return targetFixture.generateSequence(typeOf<T>(), sequenceStrategy) as Sequence<T>
    }

    /**
     * Creates a new [Fixture] deriving from this instance's configuration.
     */
    fun new(
        configuration: ConfigurationBuilder.() -> Unit = {}
    ): Fixture {
        val newConfig = ConfigurationBuilder(this.configuration).apply(configuration).build()
        return Fixture(newConfig)
    }

    @PublishedApi
    internal fun generateValue(type: KType, range: Iterable<Any?>): Any? {
        val rangeList = range.toList()
        val filterSpec = configuration.filters[type]

        if (rangeList.isNotEmpty()) {
            val eligible = if (filterSpec != null) {
                rangeList.filter { filterSpec.matchesPredicate(it) }
            } else {
                rangeList
            }

            if (eligible.isNotEmpty()) {
                return eligible[configuration.random.nextInt(eligible.size)]
            }
        }

        return generateSingleRawValue(type, filterSpec)
    }

    private fun generateSingleRawValue(type: KType, filterSpec: FilterSpec<Any?>?): Any? {
        if (filterSpec == null) {
            return rawResolve(type)
        }

        val seen = mutableSetOf<Any?>()
        var attempts = 0

        while (attempts < MAX_FILTER_ATTEMPTS) {
            attempts++
            val candidate = rawResolve(type)
            if (filterSpec.matches(candidate, seen)) {
                if (filterSpec.isDistinct) seen.add(candidate)
                return candidate
            }
        }

        error("Filter could not be satisfied after $MAX_FILTER_ATTEMPTS attempts for type $type")
    }

    @PublishedApi
    internal fun generateSequence(type: KType, sequenceStrategy: SequenceStrategy): Sequence<Any?> {
        val filterSpec = configuration.filters[type]

        val rawSeq = kotlin.sequences.generateSequence { rawResolve(type) }
            .filter { candidate -> filterSpec?.matchesPredicate(candidate) ?: true }
            .let { seq ->
                if (filterSpec?.isDistinct == true) seq.distinct() else seq
            }

        return when (sequenceStrategy) {
            is SequenceStrategy.Unbounded -> rawSeq
            is SequenceStrategy.Bounded -> {
                if (sequenceStrategy.numberOfElements == 0) {
                    emptySequence()
                } else {
                    rawSeq.take(sequenceStrategy.numberOfElements)
                }
            }
        }
    }

    private fun rawResolve(type: KType): Any? {
        val nullableStrategy = someInstance.config[NullableStrategy::class]
        val session = ResolverChain(someInstance.resolvers, nullableStrategy)
        return session.resolve(type)
    }

    companion object {
        private fun buildSomeInstance(config: Configuration): Some {
            val someConfig = config.toSomeConfig()
            val random = config.random
            val nativeResolvers = someConfig.buildResolvers(random)
            val customFactoryResolver = CompatibilityFactoryResolver(config.typeFactories, random)
            val subtypeResolver = CompatibilitySubtypeResolver(config.subTypes)

            val combinedResolvers = listOf(customFactoryResolver, subtypeResolver) + nativeResolvers
            return Some(combinedResolvers, random, someConfig)
        }
    }
}
