package ru.bartwell.kick.gradle

/**
 * Kick feature modules. Add the ones you need via `kick { modules { fileExplorer(); ktor3() } }`.
 * Room and Sqldelight pull in sqlite-runtime (and sqlite-core) under the hood.
 */
enum class KickModule {
    ControlPanel,
    FileExplorer,
    FirebaseAnalytics,
    FirebaseCloudMessaging,
    Ktor3,
    Layout,
    Logging,
    MultiplatformSettings,
    Overlay,
    Room,
    Runner,
    Sqldelight,
}
