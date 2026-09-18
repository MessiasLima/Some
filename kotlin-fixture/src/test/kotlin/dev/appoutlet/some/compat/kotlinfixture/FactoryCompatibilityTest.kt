package dev.appoutlet.some.compat.kotlinfixture

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FactoryCompatibilityTest {

    @Test
    fun `type factory resolves exact generic and nullable types`() {
        val fixture = kotlinFixture {
            factory<String> { "custom-string" }
            factory<Int?> { null }
            factory<GenericBox<Int>> { GenericBox(777) }
        }

        assertEquals("custom-string", fixture<String>())
        assertNull(fixture<Int?>())
        assertEquals(GenericBox(777), fixture<GenericBox<Int>>())
    }

    @Test
    fun `factory supports nested fixture and range calls`() {
        val fixture = kotlinFixture {
            factory<TestUser> {
                TestUser(
                    id = range(100..200),
                    name = fixture(),
                    age = range(listOf(20, 25, 30))
                )
            }
        }

        val user: TestUser = fixture()
        assertTrue(user.id in 100..200)
        assertNotNull(user.name)
        assertTrue(user.age in listOf(20, 25, 30))
    }

    @Test
    fun `factory avoids recursive self-invocation when resolving nested different types`() {
        val fixture = kotlinFixture {
            factory<String> { "hello" }
            factory<TestUser> {
                TestUser(
                    id = 42,
                    name = fixture<String>(),
                    age = 30
                )
            }
        }

        val user: TestUser = fixture()
        assertEquals(42, user.id)
        assertEquals("hello", user.name)
    }
}
