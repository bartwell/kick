package ru.bartwell.kick.gradle

/**
 * Maps [KickModule] to full Maven coordinates (group:artifact:version).
 * For SqliteRuntime in runtime mode we also need sqlite-core.
 */
internal object ModuleArtifacts {
    private const val GROUP = "ru.bartwell.kick"

    fun mainCore(version: String) = "$GROUP:main-core:$version"
    fun mainRuntime(version: String) = "$GROUP:main-runtime:$version"
    fun mainRuntimeStub(version: String) = "$GROUP:main-runtime-stub:$version"

    fun runtimeArtifacts(module: KickModule, version: String): List<String> {
        return when (module) {
            KickModule.Ktor3 -> listOf("$GROUP:ktor3:$version")
            KickModule.SqliteRuntime -> listOf("$GROUP:sqlite-core:$version", "$GROUP:sqlite-runtime:$version")
            KickModule.SqliteSqlDelightAdapter -> listOf("$GROUP:sqlite-sqldelight-adapter:$version")
            KickModule.SqliteRoomAdapter -> listOf("$GROUP:sqlite-room-adapter:$version")
            KickModule.Logging -> listOf("$GROUP:logging:$version")
            KickModule.MultiplatformSettings -> listOf("$GROUP:multiplatform-settings:$version")
            KickModule.FileExplorer -> listOf("$GROUP:file-explorer:$version")
            KickModule.Layout -> listOf("$GROUP:layout:$version")
            KickModule.FirebaseCloudMessaging -> listOf("$GROUP:firebase-cloud-messaging:$version")
        }
    }

    fun stubArtifacts(module: KickModule, version: String): List<String> {
        return when (module) {
            KickModule.Ktor3 -> listOf("$GROUP:ktor3-stub:$version")
            KickModule.SqliteRuntime -> listOf("$GROUP:sqlite-runtime-stub:$version")
            KickModule.SqliteSqlDelightAdapter -> listOf("$GROUP:sqlite-sqldelight-adapter-stub:$version")
            KickModule.SqliteRoomAdapter -> listOf("$GROUP:sqlite-room-adapter-stub:$version")
            KickModule.Logging -> listOf("$GROUP:logging-stub:$version")
            KickModule.MultiplatformSettings -> listOf("$GROUP:multiplatform-settings-stub:$version")
            KickModule.FileExplorer -> listOf("$GROUP:file-explorer-stub:$version")
            KickModule.Layout -> listOf("$GROUP:layout-stub:$version")
            KickModule.FirebaseCloudMessaging -> listOf("$GROUP:firebase-cloud-messaging-stub:$version")
        }
    }
}
