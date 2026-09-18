package dev.appoutlet.some.compat.kotlinfixture

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PropertyCompatibilityTest {

    @Test
    fun `property factory overrides parameter value`() {
        val fixture = kotlinFixture {
            property(TestUser::name) { "OverriddenName" }
            property(TestUser::age) { range(50..60) }
        }

        val user: TestUser = fixture()
        assertEquals("OverriddenName", user.name)
        assertTrue(user.age in 50..60)
    }

    @Test
    fun `property factory supports nested fixture call`() {
        val fixture = kotlinFixture {
            property(TestUser::name) { fixture<String>().uppercase() }
        }

        val user: TestUser = fixture()
        assertEquals(user.name, user.name.uppercase())
    }
}
