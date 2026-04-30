package dev.appoutlet.some.integration

import dev.appoutlet.some.some
import kotlin.test.Test
import kotlin.test.assertTrue

class GenericsIntegrationTest {
    @Test
    fun `generic wrapper class with various types`() {
        data class Wrapper<T>(val value: T)

        val stringWrapper: Wrapper<String> = some<Wrapper<String>>()
        val intWrapper: Wrapper<Int> = some<Wrapper<Int>>()

        assertTrue(stringWrapper.value.isNotEmpty())
        assertTrue(intWrapper.value is Int)
    }
}
