<p align="center">
  <img src="docs/logo-icon.svg" alt="Some logo" width="128" />
</p>

# Some

A Kotlin JVM library that generates populated instances of any Kotlin class for testing purposes, with zero configuration required.

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
- **Fine-grained control** — Override how specific fields are generated: control nullable probability, string format, collection sizes, or register custom factories for types.
- **Deterministic by choice** — Set a seed for reproducible test data across runs, or default to random for variation.

## Documentation

📖 Read the full documentation at **[some.appoutlet.dev](https://some.appoutlet.dev)** for installation, configuration, and advanced usage.

## License

Some is open source and available under the [Apache 2.0 License](LICENSE).

---

<div>
    <a href="https://appoutlet.dev">
        <img src="docs/image/appoutlet.svg" width="128" align="left" />
    </a>
</div>

### A Project by [AppOutlet](https://appoutlet.dev)

`Some` is developed and maintained by **AppOutlet**. You can explore our other projects on [our website](https://appoutlet.dev).
