package dev.appoutlet.some.compat.kotlinfixture

import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ConfigurationBuilderTest {

    @Test
    fun `ConfigurationBuilder initializes default values`() {
        val builder = ConfigurationBuilder()
        val config = builder.build()

        assertEquals(NullabilityStrategy.NeverNullStrategy, config.nullabilityStrategy)
        assertEquals(OptionalStrategy.AlwaysOptionalStrategy, config.optionalStrategy)
        assertEquals(RecursionStrategy.NullRecursionStrategy, config.recursionStrategy)
    }

    @Test
    fun `ConfigurationBuilder stores registrations`() {
        val customRandom = Random(42)
        val builder = ConfigurationBuilder().apply {
            random = customRandom
            nullabilityStrategy = NullabilityStrategy.AlwaysNullStrategy
            optionalStrategy = OptionalStrategy.NeverOptionalStrategy
            recursionStrategy = RecursionStrategy.ThrowingRecursionStrategy
            factory<Int> { 100 }
            property(TestUser::name) { "John" }
            subType<Animal, Dog>()
            filter<String> { distinct() }
        }

        val config = builder.build()

        assertEquals(customRandom, config.random)
        assertEquals(NullabilityStrategy.AlwaysNullStrategy, config.nullabilityStrategy)
        assertEquals(OptionalStrategy.NeverOptionalStrategy, config.optionalStrategy)
        assertEquals(RecursionStrategy.ThrowingRecursionStrategy, config.recursionStrategy)
        assertNotNull(config.typeFactories[typeOf<Int>()])
        assertNotNull(config.propertyFactories[TestUser::class to "name"])
        assertEquals(Dog::class, config.subTypes[Animal::class])
        assertNotNull(config.filters[typeOf<String>()])
    }
}
