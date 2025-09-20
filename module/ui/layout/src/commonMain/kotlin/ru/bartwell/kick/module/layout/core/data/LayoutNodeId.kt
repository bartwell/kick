package ru.bartwell.kick.module.layout.core.data

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
public data class LayoutNodeId(public val raw: String)
