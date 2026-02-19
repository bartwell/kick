package ru.bartwell.kick.module.logging.feature.table.util

import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIPasteboard
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.logging.core.persist.LogEntity
import ru.bartwell.kick.module.logging.feature.table.extension.toLogString

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual object LaunchUtils {
    internal actual fun canCopyLogs(): Boolean = true
    internal actual fun canSaveLogsToFile(): Boolean = false
    internal actual fun canShareLogsAsText(): Boolean = true
    internal actual fun canShareLogsAsFile(): Boolean = false

    internal actual fun copyLogs(context: PlatformContext, logs: List<LogEntity>) {
        UIPasteboard.generalPasteboard.string = logs.joinToString(separator = "\n") { it.toLogString() }
    }

    internal actual fun saveLogsToFile(context: PlatformContext, logs: List<LogEntity>) = Unit

    internal actual fun shareLogsAsText(context: PlatformContext, logs: List<LogEntity>) {
        val text = logs.joinToString(separator = "\n") { it.toLogString() }
        presentShareSheet(items = listOf(text))
    }

    internal actual fun shareLogsAsFile(context: PlatformContext, logs: List<LogEntity>) = Unit

    private fun presentShareSheet(items: List<Any>) {
        dispatch_async(dispatch_get_main_queue()) {
            val presenter = topViewController() ?: return@dispatch_async
            val controller = UIActivityViewController(
                activityItems = items,
                applicationActivities = null,
            )
            presenter.presentViewController(controller, animated = true, completion = null)
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun topViewController(): UIViewController? {
        val window = topWindow() ?: return null
        var current = window.rootViewController ?: return null
        while (current.presentedViewController != null) {
            current = current.presentedViewController!!
        }
        return current
    }

    private fun topWindow(): UIWindow? {
        val app = UIApplication.sharedApplication
        app.keyWindow?.let { return it }
        val windows = app.windows as? List<*>
        return windows?.filterIsInstance<UIWindow>()?.firstOrNull()
    }
}
