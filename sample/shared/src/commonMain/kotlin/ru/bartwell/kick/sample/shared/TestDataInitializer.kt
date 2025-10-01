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
import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.util.DateUtils
import ru.bartwell.kick.module.controlpanel.ControlPanelModule
import ru.bartwell.kick.module.controlpanel.data.ActionType
import ru.bartwell.kick.module.controlpanel.data.ControlPanelItem
import ru.bartwell.kick.module.controlpanel.data.Editor
import ru.bartwell.kick.module.controlpanel.data.InputType
import ru.bartwell.kick.module.explorer.FileExplorerModule
import ru.bartwell.kick.module.ktor3.Ktor3Module
import ru.bartwell.kick.module.logging.LoggingModule
import ru.bartwell.kick.module.logging.core.data.LogLevel
import ru.bartwell.kick.module.logging.log
import ru.bartwell.kick.module.multiplatformsettings.MultiplatformSettingsModule
import ru.bartwell.kick.module.overlay.OverlayModule
import ru.bartwell.kick.module.overlay.overlay
import ru.bartwell.kick.module.sqlite.adapter.sqldelight.SqlDelightWrapper
import ru.bartwell.kick.module.sqlite.runtime.SqliteModule
import ru.bartwell.kick.runtime.init
import ru.bartwell.kick.sample.shared.database.sqldelight.DriverFactory
import ru.bartwell.kick.sample.shared.network.SampleHttpClient
import ru.bartwell.kick.sample.shared.setting.CustomSettings
import ru.bartwell.kick.sample.shared.setting.DefaultSettings
import kotlin.time.Duration.Companion.seconds
import io.github.aakira.napier.LogLevel as NapierLogLevel

private const val DEFAULT_MAX_ITEMS: Int = 5
private const val INPUT_MIN_INT: Int = 1
private const val INPUT_MAX_INT: Int = 10

private const val PERF_KEY: String = "Performance"
private const val PERF_FPS_BASE: Int = 30
private const val PERF_FPS_MOD: Int = 31
private const val PERF_CPU_MOD: Int = 100
private const val PERF_HEAP_BASE_MB: Int = 128
private const val PERF_HEAP_MOD: Int = 64

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
            createRoomModule(context)?.let { module(it) }
            module(LoggingModule(context))
            module(Ktor3Module(context))
            module(MultiplatformSettingsModule(listOf("Default" to defaultSettings, "Custom" to customSettings)))
            module(FileExplorerModule())
            createLayoutModule(context)?.let { module(it) }
            module(ControlPanelModule(context, createControlPanelItems()))
            module(OverlayModule(context))
        }

        startTestLogging()
        makeTestHttpRequest()
        startOverlayUpdater()
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

    private fun startOverlayUpdater() {
        CoroutineScope(Dispatchers.Default).launch {
            var counter = 0L
            while (isActive) {
                Kick.overlay.set("counter", counter)
                Kick.overlay.set("config", true)
                Kick.overlay.set("timestamp", DateUtils.currentTimeMillis())
                // Extra values under a separate category to demonstrate switching
                Kick.overlay.set("fps", PERF_FPS_BASE + (counter % PERF_FPS_MOD).toInt(), PERF_KEY)
                Kick.overlay.set("cpu", (counter % PERF_CPU_MOD).toInt(), PERF_KEY)
                Kick.overlay.set("heapMb", PERF_HEAP_BASE_MB + (counter % PERF_HEAP_MOD).toInt(), PERF_KEY)
                counter++
                delay(1.seconds)
            }
        }
    }

    private fun createControlPanelItems() = listOf(
        ControlPanelItem(
            name = "featureEnabled",
            category = "General",
            type = InputType.Boolean(true),
        ),
        ControlPanelItem(
            name = "maxItems",
            type = InputType.Int(DEFAULT_MAX_ITEMS),
            editor = Editor.InputNumber(min = INPUT_MIN_INT.toDouble(), max = INPUT_MAX_INT.toDouble()),
        ),
        ControlPanelItem(
            name = "endpoint",
            category = "General",
            type = InputType.String("https://example.com"),
            editor = Editor.InputString(singleLine = true),
        ),
        ControlPanelItem(
            name = "list",
            type = InputType.String("Item 2"),
            editor = Editor.List(
                listOf(
                    InputType.String("Item 1"),
                    InputType.String("Item 2"),
                    InputType.String("Item 3"),
                )
            ),
        ),
        ControlPanelItem(
            name = "Log A",
            category = "Actions",
            type = ActionType.Button("aaaaaa"),
        ),
        ControlPanelItem(
            name = "Log B",
            category = "Actions",
            type = ActionType.Button("bbbbbb"),
        ),
    )
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect fun createRoomModule(context: PlatformContext): Module?
expect fun createLayoutModule(context: PlatformContext): Module?
