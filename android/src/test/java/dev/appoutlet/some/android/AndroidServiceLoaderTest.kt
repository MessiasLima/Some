package dev.appoutlet.some.android

import dev.appoutlet.some.some
import dev.appoutlet.some.someSetup
import org.junit.Assert.assertTrue
import org.junit.Test

class AndroidServiceLoaderTest {
    @Test
    fun `AndroidTypeResolverProvider is discovered and does not break the chain`() {
        val some = someSetup { }
        val result: String = some.some()
        assertTrue("Generated string should not be empty", result.isNotEmpty())
    }

    @Test
    fun `top-level some also works with AndroidTypeResolverProvider`() {
        val result: String = some()
        assertTrue("Generated string should not be empty", result.isNotEmpty())
    }
}
