package dev.appoutlet.some.retrofit

import dev.appoutlet.some.some
import retrofit2.Response
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class RetrofitIntegrationTest {
    data class User(val name: String, val age: Int)

    @Test
    fun `top-level some discovers Retrofit Response resolver`() {
        val result: Response<User> = some()

        assertEquals(200, result.code())
        assertNotNull(result.body())
    }
}
