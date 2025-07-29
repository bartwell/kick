package ru.bartwell.kick.module.explorer.feature.view.presentation

internal data class FileViewerState(
    val fileName: String = "",
    val text: String = "",
    val error: String? = null,
)
