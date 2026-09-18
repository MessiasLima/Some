package dev.appoutlet.some.compat.kotlinfixture

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MigrationApiCompilationTest {

    @Test
    fun `migrated code using wildcard import compiles and executes correctly`() {
        val fixture: Fixture = kotlinFixture {
            nullabilityStrategy = NullabilityStrategy.NeverNullStrategy
            optionalStrategy = OptionalStrategy.AlwaysOptionalStrategy
            recursionStrategy = RecursionStrategy.NullRecursionStrategy

            factory<String> { "migrated" }
            property(TestUser::id) { range(1..100) }
            subType<Animal, Dog>()
            filter<Int> { filter { it > 0 } }
        }

        val user: TestUser = fixture()
        val animal: Animal = fixture()
        val sequence: List<Int> = fixture.asSequence<Int>(SequenceStrategy.Bounded(3)).toList()

        assertEquals("migrated", user.name)
        assertNotNull(animal)
        assertEquals(3, sequence.size)
    }
}
