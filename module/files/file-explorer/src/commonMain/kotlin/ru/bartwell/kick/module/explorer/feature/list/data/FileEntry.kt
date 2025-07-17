package ru.bartwell.kick.module.explorer.feature.list.data

internal data class FileEntry(
    val name: String,
    val isDirectory: Boolean,
    val size: Long?,
    val lastModified: Long
)
