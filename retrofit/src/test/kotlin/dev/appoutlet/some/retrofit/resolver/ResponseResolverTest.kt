package dev.appoutlet.some.retrofit.resolver

import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import retrofit2.Response
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ResponseResolverTest {
    private val resolver = ResponseResolver()

    @Test
    fun `ResponseResolver generates Response by delegating body type to chain`() {
        val resolvedTypes = mutableListOf<KType>()
        val chain = ResolverChain(
            listOf(TrackingResolver(typeOf<String>(), "body", resolvedTypes)),
            NullableStrategy.NullOnCircularReference,
        )

        val result = resolver.resolve(typeOf<Response<String>>(), chain)

        assertIs<Response<*>>(result)
        assertEquals("body", result.body())
        assertEquals(listOf(typeOf<String>()), resolvedTypes)
        assertTrue(result.isSuccessful)
        assertEquals(200, result.code())
    }

    @Test
    fun `ResponseResolver canResolve detects Retrofit Response types`() {
        assertTrue(resolver.canResolve(typeOf<Response<String>>()))
    }

    @Test
    fun `ResponseResolver rejects non-Response types`() {
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
        assertFalse(resolver.canResolve(typeOf<okhttp3.Response>()))
    }

    @Test
    fun `ResponseResolver throws error on star projection`() {
        assertFailsWith<IllegalArgumentException> {
            resolver.resolve(
                typeOf<Response<*>>(),
                ResolverChain(
                    emptyList(),
                    NullableStrategy.NullOnCircularReference,
                ),
            )
        }
    }

    private class TrackingResolver(
        private val supportedType: KType,
        private val value: Any,
        private val resolvedTypes: MutableList<KType>,
    ) : Resolver {
        override fun canResolve(type: KType): Boolean = type == supportedType

        override fun resolve(type: KType, chain: ResolverChain): Any {
            resolvedTypes += type
            return value
        }
    }
}
