package ru.bartwell.kick.module.layout.core.data

import androidx.compose.runtime.Immutable

@Immutable
public data class LayoutNodeSnapshot(
    public val id: LayoutNodeId,
    public val typeName: String,
    public val displayName: String,
    public val bounds: LayoutRect?,
    public val isVisible: Boolean?,
    public val testTag: String?,
    public val children: List<LayoutNodeSnapshot>,
)
