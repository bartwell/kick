package ru.bartwell.kick.module.layout.core.data

import androidx.compose.runtime.Immutable

@Immutable
public data class LayoutProperty(
    public val name: String,
    public val value: String,
)
