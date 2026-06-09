package dev.appoutlet.some.resolver

import dev.appoutlet.some.exception.SomeInstantiationException
import dev.appoutlet.some.some
import dev.appoutlet.some.test.defaultTestChain
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

abstract class UnresolvableBase

class ConcreteUnresolvableBase : UnresolvableBase()

class ClassResolverTest {
    private val resolver = ClassResolver(random = Random.Default)

    @Test
    fun `ClassResolver resolves data class with primary constructor`() {
        val result = resolver.resolve(typeOf<SimpleData>(), defaultTestChain)
        assertTrue(result is SimpleData)
    }

    @Test
    fun `ClassResolver resolves class with secondary constructor only`() {
        val result = resolver.resolve(typeOf<SecondaryOnlyClass>(), defaultTestChain)
        assertTrue(result is SecondaryOnlyClass)
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
        assertTrue(resolver.canResolve(typeOf<SimpleData>()))
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
    fun `ClassResolver canResolve rejects String type`() {
        assertFalse(resolver.canResolve(typeOf<String>()))
    }

    @Test
    fun `ClassResolver canResolve rejects Int type`() {
        assertFalse(resolver.canResolve(typeOf<Int>()))
    }

    @Test
    fun `ClassResolver canResolve rejects List type`() {
        assertFalse(resolver.canResolve(typeOf<List<String>>()))
    }

    @Test
    fun `ClassResolver canResolve rejects Set type`() {
        assertFalse(resolver.canResolve(typeOf<Set<String>>()))
    }

    @Test
    fun `ClassResolver canResolve rejects Map type`() {
        assertFalse(resolver.canResolve(typeOf<Map<String, Int>>()))
    }

    @Test
    fun `ClassResolver canResolve rejects abstract class`() {
        assertFalse(resolver.canResolve(typeOf<UnresolvableBase>()))
    }

    @Test
    fun `ClassResolver canResolve rejects sealed class`() {
        assertFalse(resolver.canResolve(typeOf<TestSealed>()))
    }

    @Test
    fun `ClassResolver preserves primary constructor success path for data classes`() {
        val result = some<SimpleData>()
        assertTrue(result.name.isNotEmpty())
    }

    @Test
    fun `ClassResolver resolves class with private constructor via reflection`() {
        val result = resolver.resolve(typeOf<PrivateConstructorClass>(), defaultTestChain)
        assertTrue(result is PrivateConstructorClass)
    }
}

data class SimpleData(val name: String)

class SecondaryOnlyClass private constructor(val value: String) {
    constructor(chars: List<Char>) : this(chars.joinToString(""))
}

class FallbackClass private constructor(val name: String, val extra: UnresolvableBase) {
    constructor(name: String) : this(name, ConcreteUnresolvableBase())
}

class EmptyConstructorClass {
    val greeting: String = "hello"
}

class UnresolvableParamClass(val param: UnresolvableBase)

class PrivateConstructorClass private constructor(val secret: String)

sealed class TestSealed {
    data class Sub(val x: Int) : TestSealed()
}
