# ZonedDateTime Strategy

Controls how `java.time.ZonedDateTime` values are generated during fixture generation.

This strategy is part of the shared API available through both `some-core` and `some-android`.

Use it when your tests care about **which part of time** a value comes from, or whether the generated `ZonedDateTime` should use a **specific `ZoneId`**.

## Variants

### `Default`

Generates values across the full `Instant` range with a random JVM `ZoneId`.

```kotlin
some {
    strategy(ZonedDateTimeStrategy.Default)
}
```

This is the default strategy.

Use it when the exact date range does not matter and you only need a valid `ZonedDateTime`.

### `NearPast`

Generates values from the last 10 years.

```kotlin
some {
    strategy(ZonedDateTimeStrategy.NearPast)
}
```

Use it for features that should behave like recent historical data: imported records, audit events, or recently completed jobs.

### `NearFuture`

Generates values from now until 10 years in the future.

```kotlin
some {
    strategy(ZonedDateTimeStrategy.NearFuture)
}
```

Use it for bookings, scheduled notifications, expirations, or other forward-looking dates.

### `DistantPast`

Generates values from `Instant.MIN` until now.

```kotlin
some {
    strategy(ZonedDateTimeStrategy.DistantPast)
}
```

Use it when you want older dates to be possible, not just values from the last few years.

### `DistantFuture`

Generates values from now until `Instant.MAX`.

```kotlin
some {
    strategy(ZonedDateTimeStrategy.DistantFuture)
}
```

Use it when your domain supports far-future timestamps and a 10-year window would be too narrow.

### `Range`

Generates values between a custom `min` and `max` `Instant`, with an optional fixed `ZoneId`.

```kotlin
val min = Instant.parse("2024-01-01T00:00:00Z")
val max = Instant.parse("2024-12-31T23:59:59Z")

some {
    strategy(ZonedDateTimeStrategy.Range(min, max))
}
```

When `zoneId` is not provided, Some uses a random available JVM zone.

```kotlin
val min = Instant.parse("2024-01-01T00:00:00Z")
val max = Instant.parse("2024-12-31T23:59:59Z")

some {
    strategy(
        ZonedDateTimeStrategy.Range(
            min = min,
            max = max,
            zoneId = ZoneId.of("America/Sao_Paulo"),
        )
    )
}
```

Use `Range` when your test should stay inside a business window such as a quarter, a release cycle, or a reporting period.

## Choosing the right variant

| Variant | Best when you need |
|---------|--------------------|
| `Default` | Any valid `ZonedDateTime` |
| `NearPast` | Recent historical values |
| `NearFuture` | Upcoming values |
| `DistantPast` | Potentially very old values |
| `DistantFuture` | Potentially very far future values |
| `Range` | A specific time window, optionally with a fixed zone |

## Zone behavior

- `Default`, `NearPast`, `NearFuture`, `DistantPast`, and `DistantFuture` use a **random available JVM `ZoneId`**.
- `Range(min, max)` also uses a random available JVM `ZoneId`.
- `Range(min, max, zoneId)` pins every generated value to the supplied `ZoneId`.

If your assertions depend on a specific zone, prefer `Range(..., zoneId = ...)`.

## Reusing the strategy

When several tests need the same time window, configure it once with `someSetup`:

```kotlin
val fixtures = someSetup {
    strategy(
        ZonedDateTimeStrategy.Range(
            min = Instant.parse("2024-01-01T00:00:00Z"),
            max = Instant.parse("2024-12-31T23:59:59Z"),
            zoneId = ZoneId.of("Europe/London"),
        )
    )
}

val startedAt: ZonedDateTime = fixtures()
val finishedAt: ZonedDateTime = fixtures()
```

## Validation and caveats

### `Range` requires `min <= max`

`ZonedDateTimeStrategy.Range` throws `IllegalArgumentException` when `min` is after `max`.

```kotlin
ZonedDateTimeStrategy.Range(
    min = Instant.parse("2025-01-01T00:00:00Z"),
    max = Instant.parse("2024-01-01T00:00:00Z"),
) // IllegalArgumentException
```

### Prefer ranges that span at least one second

!!! warning "Current resolver behavior"

    `JavaZonedDateTimeResolver` currently randomizes using epoch seconds.
    In practice, this means `Range` works best when `min` and `max` span at least one second.

If you need a stable single timestamp, register a [custom factory](../custom-factories.md) for `ZonedDateTime` in that test.

```kotlin
val fixedTime = ZonedDateTime.parse("2024-06-01T10:15:30+01:00[Europe/London]")

val fixture = some<Report> {
    factory(ZonedDateTime::class) { fixedTime }
}
```

## Summary table

| Strategy | Behavior |
|----------|----------|
| `Default` | Full `Instant` range with a random `ZoneId` (default) |
| `NearPast` | From 10 years before now until now |
| `NearFuture` | From now until 10 years after now |
| `DistantPast` | From `Instant.MIN` until now |
| `DistantFuture` | From now until `Instant.MAX` |
| `Range(min, max)` | Custom range with a random `ZoneId` |
| `Range(min, max, zoneId)` | Custom range pinned to a specific `ZoneId` |
