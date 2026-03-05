package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.CollectionStrategy
import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.test.defaultTestChain
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SetResolverTest {
    @Test
    fun `SetResolver generates set with correct size`() {
        val config = SomeConfig().apply {
            collectionStrategy = CollectionStrategy(3..5)
        }
        val resolvers = config.buildResolvers()
        val resolver = SetResolver(CollectionStrategy(3..5), Random.Default)
        
        val result = resolver.resolve(typeOf<Set<String>>(), ResolverChain(resolvers))
        assertIs<Set<*>>(result)
        assertTrue(result.size in 3..5)
    }
    
    @Test
    fun `SetResolver generates set with elements of correct type`() {
        val resolver = SetResolver(CollectionStrategy(), Random.Default)
        
        val result = resolver.resolve(typeOf<Set<String>>(), defaultTestChain)
        assertIs<Set<*>>(result)
        assertTrue(result.all { it is String })
    }
    
    @Test
    fun `SetResolver generates set with Int elements`() {
        val resolver = SetResolver(CollectionStrategy(), Random.Default)
        
        val result = resolver.resolve(typeOf<Set<Int>>(), defaultTestChain)
        assertIs<Set<*>>(result)
        assertTrue(result.all { it is Int })
    }


    @Test
    fun `SetResolver generates set of data class`() {
        data class DataClass(val a: Int, val b: String)
        val resolver = SetResolver(CollectionStrategy(), Random.Default)

        val result = resolver.resolve(typeOf<Set<DataClass>>(), defaultTestChain)
        assertIs<Set<DataClass>>(result)
    }

    @Test
    fun `SetResolver generates MutableSet when requested`() {
        val resolver = SetResolver(CollectionStrategy(), Random.Default)
        
        val result = resolver.resolve(typeOf<MutableSet<String>>(), defaultTestChain)
        assertIs<MutableSet<*>>(result)
    }
    
    @Test
    fun `SetResolver canResolve detects Set types`() {
        val resolver = SetResolver(CollectionStrategy(), Random.Default)
        assertTrue(resolver.canResolve(typeOf<Set<String>>()))
        assertTrue(resolver.canResolve(typeOf<Set<Int>>()))
        assertTrue(resolver.canResolve(typeOf<MutableSet<String>>()))
    }
    
    @Test
    fun `SetResolver rejects non-Set types`() {
        val resolver = SetResolver(CollectionStrategy(), Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
        assertFalse(resolver.canResolve(typeOf<List<String>>()))
    }
    
    @Test
    fun `SetResolver throws error on star projection`() {
        val resolver = SetResolver(CollectionStrategy(), Random.Default)
        
        assertFailsWith<IllegalStateException> {
            resolver.resolve(typeOf<Set<*>>(), defaultTestChain)
        }
    }
}
