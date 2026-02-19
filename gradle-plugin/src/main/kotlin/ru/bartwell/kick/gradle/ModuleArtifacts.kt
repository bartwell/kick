package ru.bartwell.kick.gradle

/**
 * Maps [KickModule] to full Maven coordinates (group:artifact:version).
 * Room and Sqldelight include sqlite-runtime (and sqlite-core for runtime) under the hood.
 */
internal object ModuleArtifacts {
    private const val GROUP = "ru.bartwell.kick"

    fun mainCore(version: String) = "$GROUP:main-core:$version"
    fun mainRuntime(version: String) = "$GROUP:main-runtime:$version"
    fun mainRuntimeStub(version: String) = "$GROUP:main-runtime-stub:$version"

    fun runtimeArtifacts(module: KickModule, version: String): List<String> {
        return when (module) {
            KickModule.ControlPanel -> listOf("$GROUP:control-panel:$version")
            KickModule.FileExplorer -> listOf("$GROUP:file-explorer:$version")
            KickModule.FirebaseAnalytics -> listOf("$GROUP:firebase-analytics:$version")
            KickModule.FirebaseCloudMessaging -> listOf("$GROUP:firebase-cloud-messaging:$version")
            KickModule.Ktor3 -> listOf("$GROUP:ktor3:$version")
            KickModule.Layout -> listOf("$GROUP:layout:$version")
            KickModule.Logging -> listOf("$GROUP:logging:$version")
            KickModule.MultiplatformSettings -> listOf("$GROUP:multiplatform-settings:$version")
            KickModule.Overlay -> listOf("$GROUP:overlay:$version")
            KickModule.Room -> listOf(
                "$GROUP:sqlite-core:$version",
                "$GROUP:sqlite-runtime:$version",
                "$GROUP:sqlite-room-adapter:$version"
            )
            KickModule.Runner -> listOf("$GROUP:runner:$version")
            KickModule.Sqldelight -> listOf(
                "$GROUP:sqlite-core:$version",
                "$GROUP:sqlite-runtime:$version",
                "$GROUP:sqlite-sqldelight-adapter:$version"
            )
        }
    }

    fun stubArtifacts(module: KickModule, version: String): List<String> {
        return when (module) {
            KickModule.ControlPanel -> listOf("$GROUP:control-panel-stub:$version")
            KickModule.FileExplorer -> listOf("$GROUP:file-explorer-stub:$version")
            KickModule.FirebaseAnalytics -> listOf("$GROUP:firebase-analytics-stub:$version")
            KickModule.FirebaseCloudMessaging -> listOf("$GROUP:firebase-cloud-messaging-stub:$version")
            KickModule.Ktor3 -> listOf("$GROUP:ktor3-stub:$version")
            KickModule.Layout -> listOf("$GROUP:layout-stub:$version")
            KickModule.Logging -> listOf("$GROUP:logging-stub:$version")
            KickModule.MultiplatformSettings -> listOf("$GROUP:multiplatform-settings-stub:$version")
            KickModule.Overlay -> listOf("$GROUP:overlay-stub:$version")
            KickModule.Room -> listOf(
                "$GROUP:sqlite-core:$version",
                "$GROUP:sqlite-runtime-stub:$version",
                "$GROUP:sqlite-room-adapter-stub:$version"
            )
            KickModule.Runner -> listOf("$GROUP:runner-stub:$version")
            KickModule.Sqldelight -> listOf(
                "$GROUP:sqlite-core:$version",
                "$GROUP:sqlite-runtime-stub:$version",
                "$GROUP:sqlite-sqldelight-adapter-stub:$version"
            )
        }
    }
}
