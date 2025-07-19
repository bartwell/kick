package ru.bartwell.kick.runtime.core.util

import ru.bartwell.kick.Kick
import ru.bartwell.kick.core.data.PlatformContext
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.Taskbar

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING", "EmptyFunctionBlock")
internal actual object ShortcutManager {
    internal actual fun setup(context: PlatformContext) {
        val taskbar = Taskbar.getTaskbar()
        if (taskbar.isSupported(Taskbar.Feature.MENU)) {
            val popup = taskbar.menu ?: PopupMenu().also { taskbar.menu = it }
            popup.removeAll()
            popup.add(MenuItem(title)).also { item ->
                item.addActionListener { Kick.launch(context) }
            }
        }
    }
}
