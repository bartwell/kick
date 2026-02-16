package ru.bartwell.kick.gradle

import org.gradle.api.provider.SetProperty

/**
 * DSL scope for `kick { modules { ... } }`. Use method calls instead of enum references
 * so build scripts don't need plugin types on classpath.
 * room() and sqldelight() pull in sqlite-runtime (and sqlite-core) under the hood.
 */
@Suppress("TooManyFunctions")
class KickModulesScope(
    private val modules: SetProperty<KickModule>
) {
    fun controlPanel() = modules.add(KickModule.ControlPanel)
    fun fileExplorer() = modules.add(KickModule.FileExplorer)
    fun firebaseAnalytics() = modules.add(KickModule.FirebaseAnalytics)
    fun firebaseCloudMessaging() = modules.add(KickModule.FirebaseCloudMessaging)
    fun ktor3() = modules.add(KickModule.Ktor3)
    fun layout() = modules.add(KickModule.Layout)
    fun logging() = modules.add(KickModule.Logging)
    fun multiplatformSettings() = modules.add(KickModule.MultiplatformSettings)
    fun overlay() = modules.add(KickModule.Overlay)
    fun room() = modules.add(KickModule.Room)
    fun runner() = modules.add(KickModule.Runner)
    fun sqldelight() = modules.add(KickModule.Sqldelight)
}
