package dev.appoutlet.some.compat.kotlinfixture

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class RandomCompatibilityTest {

    @Test
    fun `deterministic seed produces identical fixture sequence`() {
        val seed = 12345L

        val fixture1 = kotlinFixture {
            random = Random(seed)
        }

        val fixture2 = kotlinFixture {
            random = Random(seed)
        }

        val list1 = List(5) { fixture1<Int>() }
        val list2 = List(5) { fixture2<Int>() }

        assertEquals(list1, list2)
    }

    @Test
    fun `configured random is preserved in range and factories`() {
        val seed = 9999L

        val fixture1 = kotlinFixture {
            random = Random(seed)
            factory<TestUser> {
                TestUser(
                    id = range(1..1000),
                    name = fixture()
                )
            }
        }

        val fixture2 = kotlinFixture {
            random = Random(seed)
            factory<TestUser> {
                TestUser(
                    id = range(1..1000),
                    name = fixture()
                )
            }
        }

        val user1: TestUser = fixture1()
        val user2: TestUser = fixture2()

        assertEquals(user1, user2)
    }
}
