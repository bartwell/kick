package ru.bartwell.kick.module.overlay

import org.junit.Assume
import org.junit.Test
import ru.bartwell.kick.core.data.getPlatformContext
import ru.bartwell.kick.module.overlay.core.overlay.KickOverlay
import ru.bartwell.kick.module.overlay.core.persists.OverlaySettings
import java.awt.GraphicsEnvironment
import java.awt.Window
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
        // Wait up to 2 seconds for window to appear
        run {
            val start = System.currentTimeMillis()
            while (System.currentTimeMillis() - start < 2000) {
                val ok = Window.getWindows().any { it.isVisible && it.javaClass.simpleName == "JWindow" }
                if (ok) break
                Thread.sleep(50)
            }
        }
        assertTrue(OverlaySettings.isEnabled())

        val hasVisibleJWindow = Window.getWindows().any { it.isVisible && it.javaClass.simpleName == "JWindow" }
        assertTrue(hasVisibleJWindow)

        // Hide
        KickOverlay.hide()
        Thread.sleep(200)
        assertFalse(OverlaySettings.isEnabled())
    }
}
