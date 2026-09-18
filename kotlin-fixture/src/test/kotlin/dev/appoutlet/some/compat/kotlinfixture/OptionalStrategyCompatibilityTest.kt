package dev.appoutlet.some.compat.kotlinfixture

import kotlin.test.Test
import kotlin.test.assertEquals

class OptionalStrategyCompatibilityTest {

    @Test
    fun `AlwaysOptionalStrategy uses default constructor values`() {
        val fixture = kotlinFixture {
            optionalStrategy = OptionalStrategy.AlwaysOptionalStrategy
        }

        val user: TestUser = fixture()
        assertEquals(30, user.age)
    }

    @Test
    fun `NeverOptionalStrategy generates values for optional parameters`() {
        val fixture = kotlinFixture {
            optionalStrategy = OptionalStrategy.NeverOptionalStrategy
            factory<Int> { 999 }
        }

        val user: TestUser = fixture()
        assertEquals(999, user.age)
    }
}
