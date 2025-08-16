package ru.bartwell.kick.module.layout.core.trigger

import ru.bartwell.kick.core.data.PlatformContext
import java.awt.AWTEvent
import java.awt.Toolkit
import java.awt.event.AWTEventListener
import java.awt.event.KeyEvent

public actual class LayoutTriggerController actual constructor(
    @Suppress("UNUSED_PARAMETER") context: PlatformContext,
    private val onTrigger: () -> Unit,
) {
    private var listener: AWTEventListener? = null

    public actual fun start(enabled: Boolean) {
        if (!enabled) return
        val l = AWTEventListener { event ->
            val e = event as? KeyEvent ?: return@AWTEventListener
            if (e.id == KeyEvent.KEY_PRESSED && checkShortcut(e)) {
                onTrigger()
            }
        }
        Toolkit.getDefaultToolkit().addAWTEventListener(l, AWTEvent.KEY_EVENT_MASK)
        listener = l
    }

    public actual fun stop() {
        listener?.let { Toolkit.getDefaultToolkit().removeAWTEventListener(it) }
        listener = null
    }

    private fun checkShortcut(e: KeyEvent): Boolean {
        val isMac = System.getProperty("os.name")?.contains("Mac", true) == true
        val metaPressed = if (isMac) e.isMetaDown else e.isControlDown
        return e.keyCode == KeyEvent.VK_K && metaPressed && e.isAltDown && e.isShiftDown
    }
}
