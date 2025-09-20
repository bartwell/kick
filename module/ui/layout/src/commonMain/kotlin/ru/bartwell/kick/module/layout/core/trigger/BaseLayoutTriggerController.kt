package ru.bartwell.kick.module.layout.core.trigger

import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.util.WindowStateManager

public abstract class BaseLayoutTriggerController(
    protected val context: PlatformContext,
    protected val triggerCallback: () -> Unit,
) {
    protected fun canTrigger(): Boolean {
        return WindowStateManager.getInstance()?.isWindowOpen() != true
    }
}
