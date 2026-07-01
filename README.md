<div align="center">
  <img src="docs/logo-icon.svg" alt="Some logo" width="128" />
  <h1>Some</h1>
  
  ![Kotlin JVM](https://img.shields.io/badge/jvm-kotlin?style=for-the-badge&logo=kotlin&label=kotlin&color=%237F52FF)
  ![Kotlin Android](https://img.shields.io/badge/android-kotlin?style=for-the-badge&logo=android&label=kotlin&color=%231FA95C)
  ![Maven Central Version](https://img.shields.io/maven-central/v/dev.appoutlet/some-core?style=for-the-badge)

  
  <p>A Kotlin library to generate testing objects</p>
</div>

## The Problem

Writing tests means creating test data — lots of it. Constructing data classes, populating fields with dummy values, and keeping dependencies consistent is tedious and error-prone. As your model grows, so does the boilerplate.

```kotlin
// Without Some
val user = User(
    name = "John", age = 30,
    email = "john@example.com",
    address = Address(street = "123 Main St", city = "Springfield", zipCode = "12345"),
    orders = listOf(Order(id = "ord-1", amount = 99.99), Order(id = "ord-2", amount = 49.50))
)

// With Some
val user = some<User>()
```

**Some** eliminates boilerplate by generating fully populated instances of any Kotlin class with a single function call.

## Features

- **Zero configuration** — `some<T>()` generates a complete instance right out of the box. No builders, no factories, no setup.
- **Universal type support** — Works with data classes, sealed classes/interfaces, object singletons, value classes, generics, and all standard collections. If Kotlin can represent it, Some can generate it.
- **Nested and recursive structures** — Handles deeply nested data classes, circular references, and recursive sealed class hierarchies without infinite loops.
- **Fine-grained control** — Override how specific fields are generated: control nullable probability, string format, collection sizes, register custom type factories for types, or use property factories for individual fields.
- **Extensible** — Ship custom `ResolverProvider` implementations discovered via `ServiceLoader` to add support for domain-specific, third-party, or internal application types — with custom strategies and no consumer configuration required.
- **Deterministic by choice** — Set a seed for reproducible test data across runs, or default to random for variation.

## Installation

Some is published as three artifacts:

- `some-core` for Java and Kotlin/JVM projects
- `some-android` for Android projects. It re-exports the core API, so you do not need to add `some-core` separately.
- `some-kotest` for Kotest `Arb` integration. Add it alongside either `some-core` or `some-android`.

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

## Documentation

📖 Read the full documentation at **[some.appoutlet.dev](https://some.appoutlet.dev)** for installation, configuration, and advanced usage.

If you are upgrading from `0.2.1`, start with the [0.2.1 to 0.2.2 migration notes](docs/migration/0.2.1-to-0.2.2.md).

## Contributing

Interested in helping improve Some? Contributions are welcome. Please read the [contributing guide](CONTRIBUTING.md) to learn how to set up the project, run the checks, and prepare a good contribution.

## License

Some is open source and available under the [Apache 2.0 License](LICENSE).

---

<div>
    <a href="https://appoutlet.dev">
        <img src="docs/image/appoutlet.svg" width="128" align="left" />
    </a>
</div>

### A Project by [AppOutlet](https://appoutlet.dev)

`Some` is developed and maintained by **AppOutlet**. 

You can explore our other projects on [our website](https://appoutlet.dev).
