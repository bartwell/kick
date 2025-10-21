package ru.bartwell.kick.core.data

public enum class ModuleDescription(
    public val title: String,
    public val description: String,
) {
    SQL_DELIGHT(
        title = "SQLDelight",
        description = "View and edit the app’s SQLDelight database: browse tables, change values, verify UI."
    ),
    ROOM(
        title = "Room",
        description = "Inspect the Room database: browse tables, update fields, and confirm UI reflects changes."
    ),
    LOGGING(
        title = "Logging",
        description = "Live log viewer: watch actions/errors in real time, filter, and share when reproducing bugs."
    ),
    KTOR3(
        title = "Ktor3",
        description = "Network monitor: see requests/responses (URL, status, body) and find where a call fails."
    ),
    CONTROL_PANEL(
        title = "Control Panel",
        description = "Toggle features and options on the fly; switch endpoints, tweak limits, reset to defaults."
    ),
    MULTIPLATFORM_SETTINGS(
        title = "Settings",
        description = "Edit app preferences (key–value): test flags, tokens, and options with quick text changes."
    ),
    FILE_EXPLORER(
        title = "File Explorer",
        description = "Browse app files—caches, logs, databases. Open, share, or delete to simulate scenarios."
    ),
    LAYOUT(
        title = "Layout (Beta)",
        description = "Inspect the current screen’s UI tree: view hierarchy and key layout properties."
    ),
    OVERLAY(
        title = "Overlay",
        description = "Floating debug panel over the app that updates in real time. Shows live key/value metrics."
    ),
    FIREBASE_CLOUD_MESSAGING(
        title = "Firebase Cloud Messaging",
        description = "Inspect FCM tokens, installation id, delivered pushes and emulate notifications."
    ),
}
