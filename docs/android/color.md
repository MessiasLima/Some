---
icon: lucide/palette
---

# Compose Color

`some-android` includes a resolver for `androidx.compose.ui.graphics.Color`.

Once `some-android` is on your test classpath, `some<ComposeColor>()` works without custom factories or custom resolvers.

!!! note "Android-only feature"

    `ColorStrategy` is part of `some-android`. It is not available from `some-core`.

## Basic usage

```kotlin
import androidx.compose.ui.graphics.Color as ComposeColor
import dev.appoutlet.some.some

val color: ComposeColor = some()
```

Use an import alias like `ComposeColor` when the same file also references `android.graphics.Color`.

## Color strategy

Use `ColorStrategy` when the distribution of generated Compose colors matters for the code you are testing.

| Strategy | Default | What it generates | Good for |
|----------|---------|-------------------|----------|
| `ColorStrategy.RandomArgb` | Yes | Fully random red, green, blue, and alpha channels | General UI tests that only need any valid Compose color |
| `ColorStrategy.RandomHsl` | No | Random hue, saturation, and lightness converted to a Compose color | Tests that want a more color-model-aware distribution |
| `ColorStrategy.Palette` | No | A random value chosen from a fixed list of Compose colors | Design-system palettes, brand colors, and snapshot stability |

### Configure it per call

```kotlin
import androidx.compose.ui.graphics.Color as ComposeColor
import dev.appoutlet.some.android.strategy.ColorStrategy
import dev.appoutlet.some.some

val accent = some<ComposeColor> {
    strategy(ColorStrategy.RandomHsl)
}
```

### Configure it once and reuse it

```kotlin
import androidx.compose.ui.graphics.Color as ComposeColor
import dev.appoutlet.some.android.strategy.ColorStrategy
import dev.appoutlet.some.someSetup

val some = someSetup {
    strategy(
        ColorStrategy.Palette(
            listOf(
                ComposeColor(0.40f, 0.23f, 0.72f, 1f),
                ComposeColor(0.15f, 0.61f, 0.84f, 1f),
                ComposeColor(0.96f, 0.57f, 0.17f, 1f)
            )
        )
    )
}

val primary: ComposeColor = some()
val secondary: ComposeColor = some()
```

## What the resolvers generate

- `ColorStrategy.RandomArgb` randomizes red, green, blue, and alpha independently
- `ColorStrategy.RandomHsl` converts randomized hue, saturation, and lightness values into RGB channels
- `ColorStrategy.Palette` only returns colors from the configured palette

## Picking the right strategy

- Use `ColorStrategy.RandomArgb` when your test only needs any valid Compose color
- Use `ColorStrategy.RandomHsl` when hue or tonal distribution matters more than raw channel randomness
- Use `ColorStrategy.Palette` when your UI should stay inside a known set of allowed colors

## Examples

=== "RandomArgb (default)"

    ```kotlin
    val color: ComposeColor = some()
    ```

=== "RandomHsl"

    ```kotlin
    val color = some<ComposeColor> {
        strategy(ColorStrategy.RandomHsl)
    }
    ```

=== "Palette"

    ```kotlin
    val color = some<ComposeColor> {
        strategy(
            ColorStrategy.Palette(
                listOf(
                    ComposeColor(0.12f, 0.13f, 0.18f, 1f),
                    ComposeColor(0.87f, 0.33f, 0.29f, 1f)
                )
            )
        )
    }
    ```

See [Android](index.md) for installation and the list of Android-specific supported types.
