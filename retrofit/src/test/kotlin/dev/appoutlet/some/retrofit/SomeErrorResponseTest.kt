package dev.appoutlet.some.retrofit

import okhttp3.Headers
import okhttp3.Protocol
import okhttp3.Request
import retrofit2.Response
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SomeErrorResponseTest {
    data class User(val name: String, val age: Int)

    @Test
    fun `someErrorResponse generates an error response with empty error body`() {
        val result: Response<User> = someErrorResponse()

        assertEquals(400, result.code())
        assertEquals("Bad Request", result.message())
        assertNull(result.body())
        assertNotNull(result.errorBody())
        assertEquals("http://localhost/", result.raw().request.url.toString())
    }

    @Test
    fun `someErrorResponse preserves custom response metadata`() {
        val headers = Headers.Builder().add("X-Test", "value").build()
        val request = Request.Builder().url("https://example.com/users").build()

        val result: Response<User> = someErrorResponse(
            code = 500,
            message = "Server Error",
            protocol = Protocol.HTTP_2,
            headers = headers,
            request = request,
        )

        assertEquals(500, result.code())
        assertEquals("Server Error", result.message())
        assertEquals(Protocol.HTTP_2, result.raw().protocol)
        assertEquals(headers, result.headers())
        assertEquals(request, result.raw().request)
        assertNull(result.body())
        assertNotNull(result.errorBody())
    }

    @Test
    fun `someErrorResponse wraps a supplied raw response`() {
        val rawResponse = okhttp3.Response.Builder()
            .code(404)
            .message("Not Found")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url("https://example.com/missing").build())
            .build()

        val result: Response<User> = someErrorResponse(rawResponse)

        assertEquals(rawResponse, result.raw())
        assertEquals(404, result.code())
        assertNull(result.body())
        assertNotNull(result.errorBody())
    }

    @Test
    fun `someErrorResponse rejects successful responses`() {
        assertFailsWith<IllegalArgumentException> {
            someErrorResponse<User>(code = 200)
        }

        val successfulRawResponse = okhttp3.Response.Builder()
            .code(200)
            .message("OK")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url("http://localhost/").build())
            .build()

        assertFailsWith<IllegalArgumentException> {
            someErrorResponse<User>(successfulRawResponse)
        }
    }
}
