package ru.bartwell.kick.gradle

/**
 * Kick feature modules. Add the ones you need via `kick { modules(...) }`.
 */
enum class KickModule {
    Ktor3,
    SqliteRuntime,
    SqliteSqlDelightAdapter,
    SqliteRoomAdapter,
    Logging,
    MultiplatformSettings,
    FileExplorer,
    Layout,
    FirebaseCloudMessaging
}
