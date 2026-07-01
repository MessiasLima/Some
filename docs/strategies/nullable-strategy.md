# Nullable Strategy

Controls whether nullable types produce `null` or a concrete value during fixture generation.

The default is `NullableStrategy.NullOnCircularReference`, which prefers concrete values and only returns `null`
when a nullable type would otherwise create a circular reference.

This strategy is part of the shared API available through both `some-core` and `some-android`.

## Variants

### `NullOnCircularReference`

Produces concrete values for nullable types unless resolving the value would create a circular reference.

```kotlin
some { strategy(NullableStrategy.NullOnCircularReference) }
```

This is the default strategy.

```kotlin
data class Node(val next: Node?)

val node = some<Node>()

node.next // null
```

The nullable circular field is resolved as `null`, so generation stops instead of recursing forever.
Non-nullable circular references still throw `SomeCircularReferenceException` because no finite value can satisfy them:

```kotlin
data class StrictNode(val next: StrictNode)

some<StrictNode>() // throws SomeCircularReferenceException
```

### `AlwaysNull`

Always produces `null` for nullable types.

```kotlin
some { strategy(NullableStrategy.AlwaysNull) }
```

Use this strategy when testing edge cases or validating null-handling logic.

### `NeverNull`

Always produces a non-null concrete value for nullable types.

```kotlin
some { strategy(NullableStrategy.NeverNull) }
```

Use this strategy when you want to ensure all nullable fields are populated, which is useful for testing happy-path scenarios.

### `Random`

Produces `null` based on a configurable probability.

```kotlin
// 50% chance of null (default)
some { strategy(NullableStrategy.Random()) }

// 20% chance of null (mostly non-null values)
some { strategy(NullableStrategy.Random(probability = 0.2)) }

// 80% chance of null (mostly null values)
some { strategy(NullableStrategy.Random(probability = 0.8)) }

// Never generate null (0% chance) - equivalent to NeverNull
some { strategy(NullableStrategy.Random(probability = 0.0)) }

// Always generate null (100% chance) - equivalent to AlwaysNull
some { strategy(NullableStrategy.Random(probability = 1.0)) }
```

#### Probability parameter

| Value | Behavior |
|-------|----------|
| `0.0` | Never produces `null` (equivalent to `NeverNull`) |
| `0.5` | 50% chance (default) |
| `1.0` | Always produces `null` (equivalent to `AlwaysNull`) |

The default probability is `0.5`, giving an equal chance of null or non-null values.

## Summary table

| Strategy | Behavior |
|----------|----------|
| `NullOnCircularReference` | Produces concrete nullable values unless a circular reference requires `null` (default) |
| `AlwaysNull` | Always produces `null` for nullable types |
| `NeverNull` | Always produces a non-null value |
| `Random()` | 50% chance of `null` |
| `Random(probability)` | Custom probability of `null` (0.0-1.0) |
