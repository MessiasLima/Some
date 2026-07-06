package dev.appoutlet.some.android

import android.graphics.Point
import androidx.compose.ui.text.AnnotatedString
import dev.appoutlet.some.some
import org.junit.Assert.assertTrue
import org.junit.Test
import androidx.compose.ui.graphics.Color as ComposeColor

class AndroidServiceLoaderTest {
    @Test
    fun `top-level some also works with AndroidResolverProvider`() {
        val result: String = some()
        assertTrue("Generated string should not be empty", result.isNotEmpty())
    }

    @Test
    fun `top-level some resolves Android types through AndroidResolverProvider`() {
        val result: Any = some<Point>()
        assertTrue(result is Point)
    }

    @Test
    fun `top-level some resolves AnnotatedString through AndroidResolverProvider`() {
        val result: AnnotatedString = some()
        assertTrue("Generated AnnotatedString text should not be blank", result.text.isNotBlank())
    }

    @Test
    fun `top-level some resolves Compose Color through AndroidResolverProvider`() {
        val result: ComposeColor = some()
        assertTrue(result.alpha in 0f..1f)
    }
}
