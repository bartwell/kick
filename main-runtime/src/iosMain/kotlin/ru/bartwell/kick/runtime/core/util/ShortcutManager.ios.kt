package ru.bartwell.kick.runtime.core.util

import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationShortcutIcon
import platform.UIKit.UIApplicationShortcutIconType
import platform.UIKit.UIApplicationShortcutItem
import platform.UIKit.shortcutItems
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import ru.bartwell.kick.core.data.PlatformContext

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual object ShortcutManager {
    internal actual fun setup(context: PlatformContext) {
        val shortcutItem = UIApplicationShortcutItem(
            type = id,
            localizedTitle = title,
            localizedSubtitle = subtitle,
            icon = UIApplicationShortcutIcon.iconWithType(
                UIApplicationShortcutIconType.UIApplicationShortcutIconTypeFavorite
            ),
            userInfo = null,
        )
        dispatch_async(dispatch_get_main_queue()) {
            UIApplication.sharedApplication.shortcutItems = listOf(shortcutItem)
        }
    }
}
