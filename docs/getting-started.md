---
icon: lucide/graduation-cap
---
# Getting Started

## Installation

Some is published as three artifacts. Pick one base artifact for your platform. Add `some-kotest` only if you use Kotest property testing.

!!! info "Upgrading from an older version?"

    If you are migrating from `0.2.1`, see the
    [0.2.1 to 0.2.2 migration guide](migration/0.2.1-to-0.2.2.md).

```kotlin
dependencies {
    // Kotlin/JVM or Java tests
    testImplementation("dev.appoutlet:some-core:{version}")

    // Android tests. Includes the shared Some API, so do not also add some-core.
    testImplementation("dev.appoutlet:some-android:{version}")

    // Optional: Kotest property testing integration.
    // Add this alongside either some-core or some-android.
    testImplementation("dev.appoutlet:some-kotest:{version}")
}
```

Do not add both `some-core` and `some-android`. Android projects should use `some-android` only.

## Usage

The simplest way to use Some is with the top-level `some<T>()` function:

```kotlin
import dev.appoutlet.some.some

data class User(val name: String, val age: Int)

val user = some<User>()
// User(name=xvqkpmnz, age=42)
```

It works with nested data classes, collections, and sealed classes — everything is resolved automatically without configuration:

```kotlin
data class Address(val street: String, val city: String)
data class Person(val name: String, val address: Address)

val person: Person = some<Person>()
val users: List<User> = some<List<User>>()

sealed class PaymentMethod {
    data class Card(val last4: String) : PaymentMethod()
    data class BankTransfer(val iban: String) : PaymentMethod()
    object Cash : PaymentMethod()
}

val payment: PaymentMethod = some<PaymentMethod>()
```

### Using it in a test

Here's how Some fits into a typical JUnit 5 test. Instead of hand-crafting test data, you generate a realistic fixture with `some<T>()` and pass it to the code under test:

```kotlin
class WelcomeEmail {
    fun render(user: User) = "Hi ${user.name}, welcome! Verify your email at ${user.email}."
}

class WelcomeEmailTest {
    private val email = WelcomeEmail()

    @Test
    fun `render includes user name and email in the message`() {
        // Create the object that will be used
        val user = some<User>()
        
        // Pass the generated object on the function you want to test
        val message = email.render(user)
        
        // Do the assertions you would normally do, using your favourite assertion library
        assertTrue(user.name in message)
        assertTrue(user.email in message)
    }
}
```

### Basic configuration

When you need control over the generated data, pass a config lambda to `some`. The seed guarantees reproducibility, `StringStrategy.Readable` makes failures easier to debug, and `CollectionStrategy(2..4)` limits list sizes to a known range:

```kotlin
val user = some<User> {
    strategy(StringStrategy.Readable)
}
// User(name=string-1234, age=17)

val users = some<List<User>> {
    strategy(CollectionStrategy(2..4))
}
// [User(name=string-1234, age=17), User(name=string-5678, age=23)]
```

For more configuration use cases and details, see the [Configuration overview](configuration.md), [Strategies](strategies/index.md), and [Supported Types](supported-types.md).

## Requirements

- **Kotlin 2.3.0+** — required for `typeOf<T>()` and other type introspection APIs
- **JVM 17+** — `some-core` targets JVM 17+
- **Android minSdk 26** — use `some-android` for Android projects
