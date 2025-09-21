package ru.bartwell.kick.runtime.core.util

import ru.bartwell.kick.Kick
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.util.WindowStateManager
import java.awt.Color
import java.awt.Font
import java.awt.Image
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.RenderingHints
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.event.ActionListener
import java.awt.image.BufferedImage

private const val TRAY_ICON_SIZE_PX: Int = 16
private const val TRAY_ICON_CORNER_RADIUS_PX: Int = 4
private const val TRAY_ICON_FONT_SIZE_PT: Int = 12
private const val TRAY_ICON_TEXT: String = "K"
private const val TRAY_ICON_FONT_FAMILY: String = "SansSerif"
private const val TRAY_ICON_BG_RGB: Int = 0x2088CB

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual object ShortcutManager {

    private var trayIcon: TrayIcon? = null
    private var shutdownHook: Thread? = null

    internal actual fun setup(context: PlatformContext) {
        if (!SystemTray.isSupported()) return
        if (trayIcon != null) return

        val tray = runCatching { SystemTray.getSystemTray() }.getOrNull() ?: return

        val image = createDefaultIcon()
        val icon = TrayIcon(image, subtitle).apply { isImageAutoSize = true }

        val launch = ActionListener {
            if (WindowStateManager.getInstance()?.isWindowOpen() != true) {
                Kick.launch(context)
            }
        }
        icon.addActionListener(launch)

        val popup = PopupMenu().apply {
            val item = MenuItem(subtitle)
            item.addActionListener(launch)
            add(item)
        }
        icon.popupMenu = popup

        runCatching { tray.add(icon) }.onSuccess {
            trayIcon = icon
            shutdownHook = Thread { removeTrayIconSafely() }.also {
                runCatching { Runtime.getRuntime().addShutdownHook(it) }
            }
        }
    }

    private fun createDefaultIcon(): Image {
        val size = TRAY_ICON_SIZE_PX
        val img = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
        val g = img.createGraphics()
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g.color = Color(TRAY_ICON_BG_RGB)
            g.fillRoundRect(0, 0, size, size, TRAY_ICON_CORNER_RADIUS_PX, TRAY_ICON_CORNER_RADIUS_PX)
            g.color = Color.WHITE
            g.font = Font(TRAY_ICON_FONT_FAMILY, Font.BOLD, TRAY_ICON_FONT_SIZE_PT)
            val fm = g.fontMetrics
            val text = TRAY_ICON_TEXT
            val x = (size - fm.stringWidth(text)) / 2
            val y = (size - fm.height) / 2 + fm.ascent
            g.drawString(text, x, y)
        } finally {
            g.dispose()
        }
        return img
    }

    private fun removeTrayIconSafely() {
        val tray = runCatching { SystemTray.getSystemTray() }.getOrNull() ?: return
        trayIcon?.let { runCatching { tray.remove(it) } }
        trayIcon = null
    }
}
