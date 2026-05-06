<img src="logo-icon.svg" alt="Some logo" width="128" align="center" />

# Some

A Kotlin JVM library that generates populated instances of any Kotlin class for testing. Zero configuration required.

## The Problem

Writing tests means creating test data — lots of it. Constructing data classes, populating fields with dummy values, and keeping dependencies consistent is tedious and error-prone. As your model grows, so does the boilerplate.

=== "Without Some 😞"

    ```kotlin
    @Test
    fun `validate user welcome email`() {
        val user = User(
            name = "John Doe",
            age = 30,
            email = "john@example.com",
            address = Address(
                street = "123 Main St",
                city = "Springfield",
                zipCode = "12345"
            ),
            orders = listOf(
                Order(id = "ord-1", amount = 99.99),
                Order(id = "ord-2", amount = 49.50)
            )
        )

        val email = emailService.sendWelcome(user)

        assertEquals("Welcome, John!", email.subject)
        assertEquals("john@example.com", email.to)
    }
    ```

=== "With Some 🎉"

    ```kotlin
    @Test
    fun `validate user welcome email`() {
        val user: User = some<User>()

        val email = emailService.sendWelcome(user)

        assertEquals("Welcome, ${user.name}!", email.subject)
        assertEquals(user.email, email.to)
    }
    ```

**Some** eliminates boilerplate by generating fully populated instances of any Kotlin class with a single function call.

## Features

- **Zero configuration** — `some<T>()` generates a complete instance right out of the box. No builders, no factories, no setup.
- **Universal type support** — Works with data classes, sealed classes/interfaces, object singletons, value classes, generics, and all standard collections. If Kotlin can represent it, Some can generate it.
- **Nested and recursive structures** — Handles deeply nested data classes, circular references, and recursive sealed class hierarchies without infinite loops.
- **Fine-grained control** — Override how specific fields are generated: control nullable probability, string format, collection sizes, or register custom factories for types.
- **Deterministic by choice** — Set a seed for reproducible test data across runs, or default to random for variation.

## Quick start

```kotlin
import dev.appoutlet.some.some

data class User(val name: String, val age: Int)

val user: User = some<User>()
// User(name=hjklmnoq, age=42)
```

See the [Getting Started guide](getting-started.md) for installation, configuration, and advanced usage.

## Roadmap

Some is under constant development. Releases are managed and tracked in the public **[GitHub Project](https://github.com/users/MessiasLima/projects/9)**, which is the canonical source for what is planned, in progress, and shipped.

Consult the project board to see what is actively being worked on, what is planned for upcoming releases, and what has been released.

## License

Some is open source and available under the [Apache 2.0 License](https://github.com/MessiasLima/Some/blob/main/LICENSE).

---

### A Project by [AppOutlet](https://appoutlet.dev)

[![AppOutlet Logo](image/appoutlet.svg){ align=left width=128 }](https://appoutlet.dev)

`Some` is developed and maintained by **AppOutlet**. You can explore our other projects on [our website](https://appoutlet.dev).
