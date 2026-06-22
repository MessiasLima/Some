# Float Strategy

Controls the range of `Float` values generated during fixture generation.

## Configuration

The `FloatStrategy` takes a single `range` parameter — a `ClosedFloatingPointRange<Float>` specifying the inclusive minimum and inclusive maximum of generated `Float` values. The end of the range is treated as exclusive during generation, matching `random.nextDouble(from, until)` semantics.

```kotlin
// Default: 0.0 to 1.0 (exclusive end)
some { strategy(FloatStrategy()) }

// Positive range
some { strategy(FloatStrategy(0.0f..10.0f)) }

// Negative range
some { strategy(FloatStrategy(-5.0f..-1.0f)) }

// Range that spans zero
some { strategy(FloatStrategy(-1.0f..1.0f)) }
```

## Pinning a fixed value

A secondary constructor `FloatStrategy(fixed: Float)` is provided as a convenience for pinning generation to a single value. It is equivalent to `FloatStrategy(fixed..fixed)`.

```kotlin
// Pin to a single value
some { strategy(FloatStrategy(5.0f)) }

// Equivalent zero-width range
some { strategy(FloatStrategy(5.0f..5.0f)) }
```

Zero-width ranges (`start == endInclusive`) are always honored — the resolver returns the fixed value directly without invoking the random source.

## Default

| Property | Default value |
|----------|---------------|
| `range` | `0.0f..1.0f` |

The default range preserves the behavior of `random.nextFloat()` (`[0.0, 1.0)`), so existing users see no change.

## Affected types

The float strategy applies to the `Float` primitive type:

- `Float`

## Validation

The `FloatStrategy` constructor enforces one constraint on the `range` parameter via a `require()` check. It throws `IllegalArgumentException` on failure.

### Start must be less than or equal to end

`range.start` must be `<=` `range.endInclusive`. Inverted ranges are rejected; equal bounds (zero-width ranges) are allowed.

```kotlin
FloatStrategy(0.0f..1.0f)  // OK
FloatStrategy(5.0f..2.0f)  // IllegalArgumentException: range.start must be less than or equal to range.endInclusive
FloatStrategy(3.0f..3.0f)  // OK — zero-width range, always returns 3.0f
```
