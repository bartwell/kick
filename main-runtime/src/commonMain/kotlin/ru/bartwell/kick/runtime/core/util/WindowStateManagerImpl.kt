package ru.bartwell.kick.runtime.core.util

import ru.bartwell.kick.core.util.WindowStateManager
import kotlin.concurrent.Volatile

internal class WindowStateManagerImpl : WindowStateManager {
    @Volatile
    private var isWindowOpened = false

    override fun isWindowOpen(): Boolean = isWindowOpened

    override fun setWindowOpen() {
        isWindowOpened = true
    }

    override fun setWindowClosed() {
        isWindowOpened = false
    }
}
