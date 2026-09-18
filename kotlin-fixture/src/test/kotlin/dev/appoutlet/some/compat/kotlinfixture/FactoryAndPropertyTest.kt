package dev.appoutlet.some.compat.kotlinfixture

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FactoryAndPropertyTest {

    @Test
    fun `factory overrides generated values`() {
        val fixture = kotlinFixture {
            factory(String::class) { "fixed-value" }
            factory(GenericBox::class) { GenericBox(777) }
        }

        assertEquals("fixed-value", fixture<String>())
        assertEquals(GenericBox(777), fixture<GenericBox<Int>>())
    }

    @Test
    fun `factory can generate nested values`() {
        lateinit var fixture: Fixture
        fixture = kotlinFixture {
            factory(TestUser::class) {
                TestUser(
                    id = random.nextInt(100, 201),
                    name = fixture(),
                    age = 30,
                )
            }
            factory(String::class) { "nested" }
        }

        val user: TestUser = fixture()

        assertTrue(user.id in 100..200)
        assertEquals("nested", user.name)
    }

    @Test
    fun `property factory overrides constructor properties`() {
        val fixture = kotlinFixture {
            property(TestUser::name) { "overridden" }
            property(TestUser::age) { 50 }
        }

        val user: TestUser = fixture()

        assertEquals("overridden", user.name)
        assertEquals(50, user.age)
    }
}
