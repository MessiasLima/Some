# String Strategy

Controls how string values are generated during fixture generation.

## Variants

### `Random`

Generates random lowercase alphabetic strings with configurable length.

```kotlin
// Default: 8 characters → e.g., "xvqkpmnz"
some { strategy(StringStrategy.Random()) }

// Custom length: 16 characters → e.g., "abxfmkwjqztplngh"
some { strategy(StringStrategy.Random(length = 16)) }

// Short strings: 4 characters → e.g., "rqst"
some { strategy(StringStrategy.Random(length = 4)) }
```

The `length` parameter must be greater than `0`.

### `Uuid`

Generates UUID strings in the standard format (e.g., `550e8400-e29b-41d4-a716-446655440000`).

```kotlin
// e.g., "550e8400-e29b-41d4-a716-446655440000"
some { strategy(StringStrategy.Uuid) }
```

Useful when you need unique identifiers in your test data.

### `Readable`

Generates human-readable strings like `"string-1234"`.

```kotlin
// e.g., "string-1234"
some { strategy(StringStrategy.Readable) }
```

Useful for debugging and test output where recognizable values help trace failures.

## Summary table

| Strategy | Behavior |
|----------|----------|
| `Random()` | Random lowercase alphabetic, length 8 (default) |
| `Random(length)` | Random lowercase alphabetic, custom length |
| `Uuid` | UUID string (`xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx`) |
| `Readable` | Human-readable format (`string-1234`) |
