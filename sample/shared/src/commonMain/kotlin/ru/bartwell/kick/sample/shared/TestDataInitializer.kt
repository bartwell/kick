package ru.bartwell.kick.sample.shared

import io.github.aakira.napier.Antilog
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.bartwell.kick.Kick
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.explorer.FileExplorerModule
import ru.bartwell.kick.module.ktor3.Ktor3Module
import ru.bartwell.kick.module.logging.LoggingModule
import ru.bartwell.kick.module.logging.core.data.LogLevel
import ru.bartwell.kick.module.logging.log
import ru.bartwell.kick.module.multiplatformsettings.MultiplatformSettingsModule
import ru.bartwell.kick.module.sqlite.adapter.room.RoomWrapper
import ru.bartwell.kick.module.sqlite.adapter.sqldelight.SqlDelightWrapper
import ru.bartwell.kick.module.sqlite.runtime.SqliteModule
import ru.bartwell.kick.runtime.init
import ru.bartwell.kick.sample.shared.database.room.AppDatabase
import ru.bartwell.kick.sample.shared.database.room.DatabaseBuilder
import ru.bartwell.kick.sample.shared.database.sqldelight.DriverFactory
import ru.bartwell.kick.sample.shared.network.SampleHttpClient
import ru.bartwell.kick.sample.shared.setting.CustomSettings
import ru.bartwell.kick.sample.shared.setting.DefaultSettings
import kotlin.time.Duration.Companion.seconds
import io.github.aakira.napier.LogLevel as NapierLogLevel

class TestDataInitializer(context: PlatformContext) {

    @Suppress("MaxLineLength")
    private val testLogs: Array<String> = arrayOf(
        "In a world where Android apps learn from user behavior before they even realize what they want, your code becomes the real miracle worker.",
        "Every test scenario is like an expedition into the unknown depths of your codebase, where hidden bugs await discovery around every corner.",
        "When a Kotlin-based robot orders a virtual espresso, make sure it has both the right permissions and the spark of creativity to enjoy it.",
        "Compose Multiplatform will soon unite all screens into a single harmonious symphony of interfaces—are you ready for the concerto?",
        "While you sleep, the CI server runs endless test suites so that your library remains flawless and ever-prepared for battle.",
        "Your suite of unit tests should be as unbreakable as the legendary code of a mythic hero in an epic saga.",
        "Sometimes a bug is just a glimpse into the future, nudging you toward a revolutionary optimization you hadn’t yet imagined.",
        "A robust logging system records not only runtime errors but also those fleeting moments of inspiration that strike at 3 AM.",
        "Adding a thoughtful comment to each function might just be the lifesaver your teammates—and future self—will thank you for.",
        "Your next pull request could revolutionize the project’s architecture, as long as you never forget the principles of clean code and friendly tests."
    )

    init {
        val sqlDelightDriver = DriverFactory().createDriver(context)
        val roomDatabase = AppDatabase.create(DatabaseBuilder().createBuilder(context))
        val defaultSettings = DefaultSettings().settings
        val customSettings = CustomSettings(context).settings
        Napier.base(object : Antilog() {
            override fun performLog(priority: NapierLogLevel, tag: String?, throwable: Throwable?, message: String?) {
                val level = when (priority) {
                    NapierLogLevel.VERBOSE -> LogLevel.VERBOSE
                    NapierLogLevel.DEBUG -> LogLevel.DEBUG
                    NapierLogLevel.INFO -> LogLevel.INFO
                    NapierLogLevel.WARNING -> LogLevel.WARNING
                    NapierLogLevel.ERROR -> LogLevel.ERROR
                    NapierLogLevel.ASSERT -> LogLevel.ASSERT
                }
                Kick.log(level, message)
            }
        })
        Napier.base(DebugAntilog())

        Kick.init(context) {
            module(SqliteModule(SqlDelightWrapper(sqlDelightDriver)))
            module(SqliteModule(RoomWrapper(roomDatabase)))
            module(LoggingModule(context))
            module(Ktor3Module(context))
            module(MultiplatformSettingsModule(listOf("Default" to defaultSettings, "Custom" to customSettings)))
            module(FileExplorerModule())
        }
        startTestLogging()
        makeTestHttpRequest()
    }

    private fun makeTestHttpRequest() {
        CoroutineScope(Dispatchers.Default).launch {
            val client = SampleHttpClient()
            client.makeTestRequests()
        }
    }

    private fun startTestLogging() {
        CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                Napier.log(priority = NapierLogLevel.entries.random(), message = testLogs.random())
                delay(1.seconds)
            }
        }
    }
}
