package dev.appoutlet.some.retrofit

import okhttp3.Headers
import okhttp3.Protocol
import okhttp3.Request
import retrofit2.Response
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SomeResponseTest {
    data class User(val name: String, val age: Int)

    @Test
    fun `someResponse generates a successful response with generated body and defaults`() {
        val result: Response<User> = someResponse()

        assertTrue(result.isSuccessful)
        assertEquals(200, result.code())
        assertEquals("OK", result.message())
        assertEquals(Protocol.HTTP_1_1, result.raw().protocol)
        assertEquals(0, result.headers().size)
        assertEquals("http://localhost/", result.raw().request.url.toString())
        assertNotNull(result.body())
    }

    @Test
    fun `someResponse preserves custom response metadata`() {
        val headers = Headers.Builder().add("X-Test", "value").build()
        val request = Request.Builder().url("https://example.com/users").build()

        val result: Response<User> = someResponse(
            code = 201,
            message = "Created",
            protocol = Protocol.HTTP_2,
            headers = headers,
            request = request,
        )

        assertEquals(201, result.code())
        assertEquals("Created", result.message())
        assertEquals(Protocol.HTTP_2, result.raw().protocol)
        assertEquals(headers, result.headers())
        assertEquals(request, result.raw().request)
        assertNotNull(result.body())
    }

    @Test
    fun `someResponse wraps a supplied raw response`() {
        val rawResponse = okhttp3.Response.Builder()
            .code(202)
            .message("Accepted")
            .protocol(Protocol.HTTP_1_1)
            .headers(Headers.Builder().add("X-Test", "value").build())
            .request(Request.Builder().url("https://example.com").build())
            .build()

        val result: Response<User> = someResponse(rawResponse)

        assertEquals(rawResponse, result.raw())
        assertEquals(202, result.code())
        assertNotNull(result.body())
    }

    @Test
    fun `someResponse rejects a supplied unsuccessful raw response`() {
        val rawResponse = okhttp3.Response.Builder()
            .code(404)
            .message("Not Found")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url("https://example.com/missing").build())
            .build()

        val exception = assertFailsWith<IllegalArgumentException> {
            someResponse<User>(rawResponse)
        }

        assertEquals("rawResponse must be successful response", exception.message)
    }

    @Test
    fun `someResponse rejects non-success response codes`() {
        assertFailsWith<IllegalArgumentException> {
            someResponse<User>(code = 404)
        }
    }
}
