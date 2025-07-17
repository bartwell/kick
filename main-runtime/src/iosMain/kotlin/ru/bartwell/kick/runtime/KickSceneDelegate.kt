package ru.bartwell.kick.runtime

import kotlinx.cinterop.BetaInteropApi
import platform.UIKit.UIApplicationShortcutItem
import platform.UIKit.UIScene
import platform.UIKit.UISceneConnectionOptions
import platform.UIKit.UISceneSession
import platform.UIKit.UIWindowScene
import platform.UIKit.UIWindowSceneDelegateProtocol
import platform.darwin.NSObject
import ru.bartwell.kick.Kick
import ru.bartwell.kick.core.data.getPlatformContext
import ru.bartwell.kick.runtime.core.util.ShortcutManager
import ru.bartwell.kick.runtime.core.util.id

@OptIn(BetaInteropApi::class)
internal class KickSceneDelegate @OverrideInit constructor() : NSObject(), UIWindowSceneDelegateProtocol {

    override fun scene(
        scene: UIScene,
        willConnectToSession: UISceneSession,
        options: UISceneConnectionOptions
    ) {
        handleAction(options.shortcutItem)
    }

    override fun windowScene(
        windowScene: UIWindowScene,
        performActionForShortcutItem: UIApplicationShortcutItem,
        completionHandler: (Boolean) -> Unit
    ) {
        val isHandled = handleAction(performActionForShortcutItem)
        completionHandler(isHandled)
    }

    private fun handleAction(shortcutItem: UIApplicationShortcutItem?): Boolean {
        return if (shortcutItem?.type == ShortcutManager.id) {
            Kick.Companion.launch(getPlatformContext())
            true
        } else {
            false
        }
    }
}
