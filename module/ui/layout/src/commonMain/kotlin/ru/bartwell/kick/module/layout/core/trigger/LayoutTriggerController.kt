package ru.bartwell.kick.module.layout.core.trigger

import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.util.WindowStateManager

public expect class LayoutTriggerController(
    context: PlatformContext,
    onTrigger: () -> Unit,
) {
    public fun start(enabled: Boolean)
    public fun stop()
}
