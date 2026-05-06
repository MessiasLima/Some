# Nullable Strategy

Controls whether nullable types produce `null` or a concrete value during fixture generation.

## Variants

### `AlwaysNull`

Always produces `null` for nullable types.

```kotlin
some { nullableStrategy = NullableStrategy.AlwaysNull }
```

Use this strategy when testing edge cases or validating null-handling logic.

### `NeverNull`

Always produces a non-null concrete value for nullable types.

```kotlin
some { nullableStrategy = NullableStrategy.NeverNull }
```

Use this strategy when you want to ensure all nullable fields are populated, which is useful for testing the "happy path" scenarios.

### `Random`

Produces `null` based on a configurable probability.

```kotlin
// 50% chance of null (default)
some { nullableStrategy = NullableStrategy.Random() }

// 20% chance of null (mostly non-null values)
some { nullableStrategy = NullableStrategy.Random(probability = 0.2) }

// 80% chance of null (mostly null values)
some { nullableStrategy = NullableStrategy.Random(probability = 0.8) }

// Never generate null (0% chance) — equivalent to NeverNull
some { nullableStrategy = NullableStrategy.Random(probability = 0.0) }

// Always generate null (100% chance) — equivalent to AlwaysNull
some { nullableStrategy = NullableStrategy.Random(probability = 1.0) }
```

#### Probability parameter

| Value | Behavior |
|-------|----------|
| `0.0` | Never produces `null` (equivalent to `NeverNull`) |
| `0.5` | 50% chance (default) |
| `1.0` | Always produces `null` (equivalent to `AlwaysNull`) |

The default probability is `0.5`, giving an equal chance of null or non-null values — useful for testing a mix of scenarios.

## Summary table

| Strategy | Behavior |
|----------|----------|
| `AlwaysNull` | Always produces `null` for nullable types |
| `NeverNull` | Always produces a non-null value |
| `Random()` | 50% chance of `null` (default) |
| `Random(probability)` | Custom probability of `null` (0.0–1.0) |
