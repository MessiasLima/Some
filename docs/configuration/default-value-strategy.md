# Default Value Strategy

Controls whether data class constructor parameters with default values should use their Kotlin default or be replaced with a generated fixture value.

The default is `DefaultValueStrategy.UseDefault`, which preserves Kotlin default values.

## Variants

### `UseDefault`

Uses the Kotlin default constructor value for optional parameters.

```kotlin
data class User(val role: String = "guest")

val user = some<User> {
    strategy(DefaultValueStrategy.UseDefault)
}

user.role // "guest"
```

This is the default strategy.

### `Generate`

Generates a value for optional parameters through the resolver chain, ignoring the Kotlin default value.

```kotlin
data class User(val role: String = "guest")

val user = some<User> {
    strategy(DefaultValueStrategy.Generate)
}

user.role // "a-random-string"
```

Use this strategy when you need fully populated objects even for classes that declare defaults.

## Custom Property Factories

Custom property factories always take precedence over the default value strategy.

```kotlin
data class User(val role: String = "guest")

val user = some<User> {
    strategy(DefaultValueStrategy.Generate)
    property(User::role) { "admin" }
}

user.role // "admin"
```

## Summary table

| Strategy | Behavior |
|----------|----------|
| `UseDefault` | Uses Kotlin default values for optional parameters (default) |
| `Generate` | Generates fixture values for optional parameters |
