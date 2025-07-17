package ru.bartwell.kick.core.data

import androidx.compose.material3.ColorScheme

public sealed class Theme {
    public data object Light : Theme()
    public data object Dark : Theme()
    public data object Auto : Theme()
    public class Custom(public val scheme: ColorScheme) : Theme()
}
