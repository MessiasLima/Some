---
icon: lucide/network
---
# Retrofit2 integration

Use `dev.appoutlet:some-retrofit` to generate Retrofit `Response<T>` fixtures for JVM tests.
The integration supports both successful and error responses and is used alongside a direct Retrofit dependency.

## Installation

```kotlin
dependencies {
    testImplementation("com.squareup.retrofit2:retrofit:3.0.0")
    testImplementation("dev.appoutlet:some-core:{version}")
    testImplementation("dev.appoutlet:some-retrofit:{version}")
}
```

`some-retrofit` compiles against Retrofit but does not provide Retrofit transitively. Declare Retrofit directly in the consuming project.

## Generating successful responses

The Retrofit resolver is discovered automatically when `some-retrofit` is on the classpath:

```kotlin
data class User(val id: Int, val name: String)

val response: Response<User> = some()

check(response.isSuccessful)
check(response.code() == 200)
check(response.body() != null)
```

Use `someResponse<T>()` when response metadata matters:

```kotlin
val response: Response<User> = someResponse(
    code = 201,
    message = "Created",
    headers = Headers.Builder().add("X-Test", "value").build(),
    request = Request.Builder().url("https://example.com/users").build(),
)
```

The second overload accepts an existing successful `okhttp3.Response`:

```kotlin
val response: Response<User> = someResponse(rawResponse)
```

Successful response codes must be in the `200..299` range, matching Retrofit's `Response.success` contract.

## Generating error responses

Use `someErrorResponse<T>()` for HTTP error handling tests:

```kotlin
val response: Response<User> = someErrorResponse(
    code = 404,
    message = "Not Found",
)

check(response.body() == null)
check(response.errorBody() != null)
```

The raw-response overload accepts an existing `okhttp3.Response`. Error response codes must be `400` or higher.
The generated error body is empty; add application-specific error-body handling in the test when needed.
