package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.exception.SomeSubtypeMappingException
import dev.appoutlet.some.some
import dev.appoutlet.some.someSetup
import dev.appoutlet.some.test.defaultTestChain
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

sealed interface TestPayment {
    data class Card(val last4: String) : TestPayment
    data class Cash(val amount: Int) : TestPayment
}

abstract class AbstractAnimal {
    abstract val name: String
}

data class Dog(override val name: String) : AbstractAnimal()

interface Greeter {
    val message: String
}

data class Greeting(override val message: String) : Greeter

sealed class GenericResult<out T> {
    data class Success<T>(val value: T) : GenericResult<T>()
    data class Failure(val message: String) : GenericResult<Nothing>()
}

abstract class GenericParent<T>

data class FixedChild(val value: String) : GenericParent<String>()

class SelfMapped

open class CycleParent

class CycleChild : CycleParent()

class SubtypeResolverTest {
    @Test
    fun `SubtypeResolver resolves the configured subtype`() {
        val resolver = SubtypeResolver(mapOf(TestPayment::class to TestPayment.Card::class))

        val result = resolver.resolve(typeOf<TestPayment>(), defaultTestChain)

        assertIs<TestPayment.Card>(result)
        assertTrue(result.last4.isNotEmpty())
    }

    @Test
    fun `SubtypeResolver canResolve detects mapped parents only`() {
        val resolver = SubtypeResolver(mapOf(TestPayment::class to TestPayment.Card::class))

        assertTrue(resolver.canResolve(typeOf<TestPayment>()))
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<GenericResult<String>>()))
    }

    @Test
    fun `subType mapping is deterministic for sealed parents`() {
        val generator = someSetup {
            subType<TestPayment, TestPayment.Card>()
        }

        val results = List(100) { generator.some<TestPayment>() }

        assertTrue(results.all { it is TestPayment.Card })
    }

    @Test
    fun `subType mapping resolves abstract classes`() {
        val generator = someSetup {
            subType<AbstractAnimal, Dog>()
        }

        val result = generator.some<AbstractAnimal>()

        assertIs<Dog>(result)
        assertTrue(result.name.isNotEmpty())
    }

    @Test
    fun `subType mapping resolves non-sealed interfaces`() {
        val generator = someSetup {
            subType<Greeter, Greeting>()
        }

        val result = generator.some<Greeter>()

        assertIs<Greeting>(result)
        assertTrue(result.message.isNotEmpty())
    }

    @Test
    fun `subType mapping preserves matching generic arguments`() {
        val generator = someSetup {
            subType<GenericResult<String>, GenericResult.Success<String>>()
        }

        val result = generator.some<GenericResult<String>>()

        assertIs<GenericResult.Success<String>>(result)
        assertIs<String>(result.value)
    }

    @Test
    fun `subType mapping falls back when generic arity does not match`() {
        val generator = someSetup {
            subType<GenericParent<String>, FixedChild>()
        }

        val result = generator.some<GenericParent<String>>()

        assertIs<FixedChild>(result)
        assertTrue(result.value.isNotEmpty())
    }

    @Test
    fun `parent factory takes precedence over a subtype mapping`() {
        val result = some<AbstractAnimal> {
            factory(AbstractAnimal::class) { Dog("factory-dog") }
            subType<AbstractAnimal, Dog>()
        }

        assertEquals("factory-dog", result.name)
    }

    @Test
    fun `self subtype mapping fails with a clear error`() {
        assertFailsWith<SomeSubtypeMappingException> {
            some<SelfMapped> {
                subType<SelfMapped, SelfMapped>()
            }
        }
    }

    @Test
    fun `cyclic subtype mappings fail with a clear error`() {
        val config = SomeConfig(
            subTypes = mapOf(
                CycleParent::class to CycleChild::class,
                CycleChild::class to CycleParent::class,
            ),
        )
        val chain = ResolverChain(config.buildResolvers(), null)

        assertFailsWith<SomeSubtypeMappingException> {
            chain.resolve(typeOf<CycleParent>())
        }
    }
}
