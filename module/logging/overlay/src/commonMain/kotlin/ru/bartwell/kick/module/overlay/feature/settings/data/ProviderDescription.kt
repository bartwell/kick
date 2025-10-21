package ru.bartwell.kick.module.overlay.feature.settings.data

internal data class ProviderDescription(
    val name: String,
    val categories: Set<String>,
    val isAvailable: Boolean,
)
