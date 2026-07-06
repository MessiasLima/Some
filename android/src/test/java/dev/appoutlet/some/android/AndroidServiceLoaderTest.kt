package dev.appoutlet.some.android

import android.graphics.Point
import dev.appoutlet.some.some
import org.junit.Assert.assertTrue
import org.junit.Test

class AndroidServiceLoaderTest {
    @Test
    fun `top-level some also works with AndroidTypeResolverProvider`() {
        val result: String = some()
        assertTrue("Generated string should not be empty", result.isNotEmpty())
    }

    @Test
    fun `top-level some resolves Android types through AndroidResolverProvider`() {
        val result: Any = some<Point>()
        assertTrue(result is Point)
    }
}
