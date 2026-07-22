package ru.bartwell.kick.module.overlay

import org.junit.Assume
import org.junit.Test
import ru.bartwell.kick.core.data.getPlatformContext
import ru.bartwell.kick.module.overlay.core.overlay.KickOverlay
import ru.bartwell.kick.module.overlay.core.persists.OverlaySettings
import java.awt.EventQueue
import java.awt.GraphicsEnvironment
import java.awt.Window
import javax.swing.JWindow
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("FunctionNaming")
class OverlayJvmTest {
    @Test
    fun showHide_togglesSettings_andCreatesWindow() {
        // Skip on headless environments (e.g., CI without display)
        Assume.assumeFalse(GraphicsEnvironment.isHeadless())

        val ctx = getPlatformContext()
        // Initialize settings backend
        OverlaySettings(ctx)
        OverlaySettings.setEnabled(false)

        // Show
        KickOverlay.init(ctx)
        KickOverlay.show()
        assertTrue(OverlaySettings.isEnabled())

        val hasVisibleJWindow = waitForVisibleJWindow()
        assertTrue(hasVisibleJWindow)

        // Hide
        KickOverlay.hide()
        Thread.sleep(200)
        assertFalse(OverlaySettings.isEnabled())
    }

    private fun waitForVisibleJWindow(): Boolean {
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < WINDOW_WAIT_TIMEOUT_MS) {
            EventQueue.invokeAndWait { }
            if (Window.getWindows().any { it.isVisible && it is JWindow }) {
                return true
            }
            Thread.sleep(WINDOW_POLL_INTERVAL_MS)
        }
        return false
    }

    private companion object {
        const val WINDOW_WAIT_TIMEOUT_MS = 5_000L
        const val WINDOW_POLL_INTERVAL_MS = 50L
    }
}
