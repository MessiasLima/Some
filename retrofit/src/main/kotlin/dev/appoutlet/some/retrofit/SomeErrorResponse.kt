package dev.appoutlet.some.retrofit

import dev.appoutlet.some.some
import okhttp3.Headers
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

/**
 * Generates an error Retrofit response with an empty error body.
 *
 * @param T Expected response body type for the endpoint under test.
 * @param code Error HTTP status code.
 * @param message HTTP status message.
 * @param protocol HTTP protocol used by the raw response.
 * @param headers HTTP headers used by the raw response.
 * @param request Request associated with the raw response.
 * @return An error Retrofit response with a `null` body and empty error body.
 * @throws IllegalArgumentException If the generated response is successful.
 */
@JvmOverloads
inline fun <reified T> someErrorResponse(
    code: Int = 400,
    message: String = "Bad Request",
    protocol: Protocol = Protocol.HTTP_1_1,
    headers: Headers = Headers.Builder().build(),
    request: Request = defaultRequest(),
): Response<T> {
    val rawResponse = okhttp3.Response.Builder()
        .code(code)
        .message(message)
        .protocol(protocol)
        .headers(headers)
        .request(request)
        .build()

    return someErrorResponse(rawResponse)
}

/**
 * Generates an error Retrofit response with an empty error body and supplied raw response metadata.
 *
 * @param T Expected response body type for the endpoint under test.
 * @param rawResponse Non-successful OkHttp response to wrap.
 * @return An error Retrofit response with a `null` body and empty error body.
 * @throws IllegalArgumentException If [rawResponse] is successful.
 */
inline fun <reified T> someErrorResponse(rawResponse: okhttp3.Response): Response<T> {
    require(rawResponse.isSuccessful.not()) { "rawResponse must be an error response" }
    return Response.error(some<T>().toString().toResponseBody(), rawResponse)
}
