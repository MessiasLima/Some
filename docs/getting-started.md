# Getting Started

## Installation

Add the `some` library to your build file. It is available on Maven Central.

![Maven Central Version](https://img.shields.io/maven-central/v/dev.appoutlet/some?style=for-the-badge)

Replace `{version}` with the latest release shown in the badge above.

=== "Gradle (Kotlin DSL)"

    ```kotlin
    implementation("dev.appoutlet:some:{version}")
    ```

=== "Gradle (Groovy)"

    ```groovy
    implementation 'dev.appoutlet:some:{version}'
    ```

=== "Maven"

    ```xml
    <dependency>
      <groupId>dev.appoutlet</groupId>
      <artifactId>some</artifactId>
      <version>{version}</version>
    </dependency>
    ```

`kotlin-reflect` is included as a transitive dependency — you don't need to declare it separately.

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
    stringStrategy = StringStrategy.Readable
}
// User(name=string-1234, age=17)

val users = some<List<User>> {
    collectionStrategy = CollectionStrategy(2..4)
}
// [User(name=string-1234, age=17), User(name=string-5678, age=23)]
```

For more configuration use cases and details, see the [Configuration overview](configuration/overview.md).

## Requirements

- **Kotlin 2.3.0+** — required for `typeOf<T>()` and other type introspection APIs
- **JVM 21+** — Some is JVM-only; Android and Kotlin/Native are not currently supported
