package ru.bartwell.kick.core.data

public enum class ModuleDescription(
    public val title: String,
    public val description: String,
) {
    SQL_DELIGHT(
        title = "SQLDelight",
        description = "Use to check storage layer powered by SQLDelight.",
    ),
    ROOM(
        title = "Room",
        description = "Use to check storage layer built on Room.",
    ),
    LOGGING(
        title = "Logging",
        description = "Shows log output to help track issues during tests.",
    ),
    KTOR3(
        title = "Ktor3",
        description = "Executes HTTP requests with Ktor 3 for server communication tests.",
    ),
    CONFIGURATION(
        title = "Configuration",
        description = "Edit app configuration values to simulate different scenarios.",
    ),
    MULTIPLATFORM_SETTINGS(
        title = "Settings",
        description = "Manage key-value preferences to verify behaviour.",
    ),
    FILE_EXPLORER(
        title = "File Explorer",
        description = "Browse device files to confirm saved data.",
    ),
    LAYOUT(title = "Layout"),
    OVERLAY(title = "Overlay"),
}
