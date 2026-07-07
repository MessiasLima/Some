# Compose

Support for Jetpack Compose unit, geometry, text, and color types is available in the `some-android` module.

## Supported Compose types

| Type | Usage | Notes |
|------|-------|-------|
| `androidx.compose.ui.unit.Dp` | `some<Dp>()` | Random float in `0f..1000f` |
| `androidx.compose.ui.unit.TextUnit` | `some<TextUnit>()` | Random `sp` value in `0f..100f` |
| `androidx.compose.ui.geometry.Offset` | `some<Offset>()` | Random float `x` and `y` |
| `androidx.compose.ui.geometry.Size` | `some<Size>()` | Random positive float `width` and `height` |
| `androidx.compose.ui.geometry.Rect` | `some<Rect>()` | Random float bounds where `left < right` and `top < bottom` |
| `androidx.compose.ui.unit.IntOffset` | `some<IntOffset>()` | Random integer `x` and `y` |
| `androidx.compose.ui.unit.IntSize` | `some<IntSize>()` | Random positive integer `width` and `height` |
| `androidx.compose.ui.text.AnnotatedString` | `some<AnnotatedString>()` | Non-blank plain text without span styles |
| `androidx.compose.ui.graphics.Color` | `some<ComposeColor>()` | See [Compose Color](color.md) and [Color Strategy](color.md#color-strategy) |

See [Android](index.md) for installation and the rest of the Android-specific supported types.
