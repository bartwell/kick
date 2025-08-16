package ru.bartwell.kick.module.layout.core.data

import androidx.compose.runtime.Immutable

@Immutable
public data class LayoutRect(
    public val x: Int,
    public val y: Int,
    public val width: Int,
    public val height: Int,
)
