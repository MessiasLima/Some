package dev.appoutlet.some.retrofit

import dev.appoutlet.some.some
import okhttp3.Headers
import okhttp3.Protocol
import okhttp3.Request
import retrofit2.Response

private const val DEFAULT_URL = "http://localhost/"

/**
 * Generates a successful Retrofit response containing a generated [T] body.
 *
 * @param T Type of the generated response body.
 * @param code Successful HTTP status code.
 * @param message HTTP status message.
 * @param protocol HTTP protocol used by the raw response.
 * @param headers HTTP headers used by the raw response.
 * @param request Request associated with the raw response.
 * @return A successful Retrofit response containing a generated body.
 * @throws IllegalArgumentException If [code] is outside the successful HTTP status range.
 */
@JvmOverloads
inline fun <reified T> someResponse(
    code: Int = 200,
    message: String = "OK",
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

    return Response.success(some<T>(), rawResponse)
}

/**
 * Generates a successful Retrofit response containing a generated [T] body and the supplied raw response metadata.
 *
 * @param T Type of the generated response body.
 * @param rawResponse Successful OkHttp response to wrap.
 * @return A successful Retrofit response containing a generated body.
 * @throws IllegalArgumentException If [rawResponse] is not successful.
 */
inline fun <reified T> someResponse(rawResponse: okhttp3.Response): Response<T> =
    Response.success(some<T>(), rawResponse)

@PublishedApi
internal fun defaultRequest(): Request = Request.Builder().url(DEFAULT_URL).build()
