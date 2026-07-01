---
icon: lucide/sliders-horizontal
---
# Strategies

Some ships with built-in strategies for the most common test data tuning scenarios. Register them with `strategy(...)` inside `some {}` or `someSetup {}` blocks.

These strategies are part of the shared API consumed through both `some-core` and `some-android`.

```kotlin
val user = some<User> {
    strategy(StringStrategy.Readable)
    strategy(NullableStrategy.NeverNull)
    strategy(CollectionStrategy(2..4))
}
```

## Defaults

| Strategy | Default | Description |
|----------|---------|-------------|
| `NullableStrategy` | `NullableStrategy.NullOnCircularReference` | Emits `null` only when a nullable circular reference would otherwise recurse forever |
| `StringStrategy` | `StringStrategy.Random()` | Random lowercase alphabetic strings |
| `CollectionStrategy` | `CollectionStrategy()` | Collections with 1 to 5 elements |
| `FloatStrategy` | `FloatStrategy()` | Floats in the range `0.0f..1.0f` |
| `DefaultValueStrategy` | `DefaultValueStrategy.UseDefault` | Uses Kotlin defaults for optional constructor parameters |

## Available strategies

- [Nullable Strategy](nullable-strategy.md)
- [String Strategy](string-strategy.md)
- [Collection Strategy](collection-strategy.md)
- [Float Strategy](float-strategy.md)
- [Default Value Strategy](default-value-strategy.md)
