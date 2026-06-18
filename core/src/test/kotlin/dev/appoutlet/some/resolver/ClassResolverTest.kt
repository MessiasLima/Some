package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.DefaultStrategyProvider
import dev.appoutlet.some.config.DefaultValueStrategy
import dev.appoutlet.some.exception.SomeInstantiationException
import dev.appoutlet.some.test.defaultTestChain
import java.math.BigInteger
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

abstract class AbstractClass

class ConcreteClass : AbstractClass()

data class SimpleDataClass(val name: String)

data class Wrapper<T>(val value: T)

data class SimpleDataClassWithGenerics(val names: Wrapper<String>)

class SecondaryOnlyClass private constructor(val value: String) {
    constructor(chars: List<Char>) : this(chars.joinToString(""))
}

class FallbackClass private constructor(val name: String, val extra: AbstractClass) {
    constructor(name: String) : this(name, ConcreteClass())
}

class EmptyConstructorClass {
    val greeting: String = "hello"
}

class UnresolvableParamClass(val param: AbstractClass)

class PrivateConstructorClass private constructor(val secret: String)

sealed class TestSealed {
    data class Sub(val x: Int) : TestSealed()
}

data class WithDefaultParams(val name: String, val count: Int = 42, val active: Boolean = true)

@Suppress("TooGenericExceptionThrown")
class ThrowingConstructorClass(val name: String) {
    init {
        throw RuntimeException()
    }
}

class MyArrayList<E> : ArrayList<E>()

class MyHashSet<E> : HashSet<E>()

class MyHashMap<K, V> : HashMap<K, V>()

class ClassResolverTest {
    private val resolver = ClassResolver(
        strategyProvider = DefaultStrategyProvider(),
        random = Random.Default
    )

    @Test
    fun `ClassResolver resolves data class with primary constructor`() {
        val result = resolver.resolve(typeOf<SimpleDataClass>(), defaultTestChain)
        assertTrue(result is SimpleDataClass)
    }

    @Test
    fun `ClassResolver resolves data class with primary constructor with generics`() {
        val result = resolver.resolve(typeOf<SimpleDataClassWithGenerics>(), defaultTestChain)
        assertTrue(result is SimpleDataClassWithGenerics)
    }

    @Test
    fun `ClassResolver resolves class with secondary constructor only`() {
        val result = resolver.resolve(typeOf<SecondaryOnlyClass>(), defaultTestChain)
        assertTrue(result is SecondaryOnlyClass)
    }

    @Test
    fun `ClassResolver resolves external kotlin class`() {
        val result = resolver.resolve(typeOf<Pair<SimpleDataClass, FallbackClass>>(), defaultTestChain)
        val castedResult = result as Pair<SimpleDataClass, FallbackClass>
        assertTrue(castedResult.first is SimpleDataClass)
        assertTrue(castedResult.second is FallbackClass)
    }

    @Test
    fun `ClassResolver resolves external java class`() {
        val date = resolver.resolve(typeOf<java.util.Date>(), defaultTestChain)
        assertNotNull(date)
    }

    @Test
    fun `ClassResolver falls back to secondary constructor when primary fails`() {
        val result = resolver.resolve(typeOf<FallbackClass>(), defaultTestChain)
        assertTrue(result is FallbackClass)
    }

    @Test
    fun `ClassResolver resolves class with empty constructor`() {
        val result = resolver.resolve(typeOf<EmptyConstructorClass>(), defaultTestChain)
        assertTrue(result is EmptyConstructorClass)
    }

    @Test
    fun `ClassResolver throws SomeInstantiationException when all constructors fail`() {
        assertFailsWith<SomeInstantiationException> {
            resolver.resolve(typeOf<UnresolvableParamClass>(), defaultTestChain)
        }
    }

    @Test
    fun `ClassResolver canResolve accepts class with constructor`() {
        assertTrue(resolver.canResolve(typeOf<SimpleDataClass>()))
    }

    @Test
    fun `ClassResolver canResolve accepts class with secondary constructor only`() {
        assertTrue(resolver.canResolve(typeOf<SecondaryOnlyClass>()))
    }

    @Test
    fun `ClassResolver canResolve accepts class with empty constructor`() {
        assertTrue(resolver.canResolve(typeOf<EmptyConstructorClass>()))
    }

    @Test
    fun `ClassResolver canResolve rejects well known types`() {
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
        assertFalse(resolver.canResolve(typeOf<List<String>>()))
        assertFalse(resolver.canResolve(typeOf<Set<String>>()))
        assertFalse(resolver.canResolve(typeOf<Map<String, Int>>()))
        assertFalse(resolver.canResolve(typeOf<AbstractClass>()))
        assertFalse(resolver.canResolve(typeOf<TestSealed>()))
    }

    @Test
    fun `ClassResolver resolves class with private constructor via reflection`() {
        val result = resolver.resolve(typeOf<PrivateConstructorClass>(), defaultTestChain)
        assertTrue(result is PrivateConstructorClass)
    }

    @Test
    fun `ClassResolver with UseDefault strategy uses Kotlin default values for optional parameters`() {
        val result = resolver.resolve(typeOf<WithDefaultParams>(), defaultTestChain) as WithDefaultParams
        assertTrue(result.name.isNotEmpty())
        assertEquals(42, result.count)
        assertTrue(result.active)
    }

    @Test
    fun `ClassResolver with Generate strategy generates values for optional parameters`() {
        val generateResolver = ClassResolver(
            strategyProvider = DefaultStrategyProvider(
                mapOf(DefaultValueStrategy::class to DefaultValueStrategy.Generate)
            ),
            random = Random.Default
        )

        val results = List(100) {
            generateResolver.resolve(typeOf<WithDefaultParams>(), defaultTestChain) as WithDefaultParams
        }
        val hasNonDefaultCount = results.any { it.count != 42 }
        val hasNonDefaultActive = results.any { !it.active }
        assertTrue(
            hasNonDefaultCount || hasNonDefaultActive,
            "Expected at least one generated value to differ from Kotlin defaults"
        )
    }

    @Test
    fun `ClassResolver canResolve rejects List subclass`() {
        assertFalse(resolver.canResolve(typeOf<MyArrayList<String>>()))
    }

    @Test
    fun `ClassResolver canResolve rejects Set subclass`() {
        assertFalse(resolver.canResolve(typeOf<MyHashSet<String>>()))
    }

    @Test
    fun `ClassResolver canResolve rejects Map subclass`() {
        assertFalse(resolver.canResolve(typeOf<MyHashMap<String, Int>>()))
    }

    @Test
    fun `ClassResolver canResolve rejects Number subclass`() {
        assertFalse(resolver.canResolve(typeOf<BigInteger>()))
    }

    @Test
    fun `ClassResolver throws SomeInstantiationException with null message constructor exception`() {
        val exception = assertFailsWith<SomeInstantiationException> {
            resolver.resolve(typeOf<ThrowingConstructorClass>(), defaultTestChain)
        }
        assertTrue(exception.message!!.contains("InvocationTargetException"))
    }
}
