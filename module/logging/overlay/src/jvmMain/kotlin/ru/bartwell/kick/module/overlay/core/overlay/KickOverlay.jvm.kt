package ru.bartwell.kick.module.overlay.core.overlay

import androidx.compose.ui.awt.ComposePanel
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.overlay.core.persists.OverlaySettings
import java.awt.AWTEvent
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.EventQueue
import java.awt.Point
import java.awt.Rectangle
import java.awt.Toolkit
import java.awt.event.AWTEventListener
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.JWindow

private const val INITIAL_WINDOW_X = 50
private const val INITIAL_WINDOW_Y = 200

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
public actual object KickOverlay {
    private var window: JWindow? = null
    private var awtListener: AWTEventListener? = null

    @Suppress("EmptyFunctionBlock")
    public actual fun init(context: PlatformContext) {}

    public actual fun show(context: PlatformContext) {
        window?.let {
            it.isVisible = true
            it.toFront()
            return
        }
        OverlaySettings.setEnabled(true)

        EventQueue.invokeLater {
            val window = JWindow().apply {
                isAlwaysOnTop = true
                background = Color(0, 0, 0, 0)
                layout = BorderLayout()
            }

            (window.rootPane as JComponent).isOpaque = false
            (window.contentPane as JComponent).apply {
                isOpaque = false
                background = Color(0, 0, 0, 0)
            }

            val panel = ComposePanel().apply {
                isOpaque = false
                background = Color(0, 0, 0, 0)
            }

            val tx = window.graphicsConfiguration?.defaultTransform
            val scaleX = tx?.scaleX ?: 1.0
            val scaleY = tx?.scaleY ?: 1.0

            var dragging = false
            var pressOffsetX = 0
            var pressOffsetY = 0
            val listener = AWTEventListener { ev ->
                if (ev !is MouseEvent) return@AWTEventListener
                when (ev.id) {
                    MouseEvent.MOUSE_PRESSED -> {
                        val b = Rectangle(window.x, window.y, window.width, window.height)
                        if (b.contains(ev.xOnScreen, ev.yOnScreen)) {
                            dragging = true
                            pressOffsetX = ev.xOnScreen - window.x
                            pressOffsetY = ev.yOnScreen - window.y
                            window.toFront()
                        }
                    }
                    MouseEvent.MOUSE_DRAGGED -> if (dragging) {
                        window.setLocation(ev.xOnScreen - pressOffsetX, ev.yOnScreen - pressOffsetY)
                    }
                    MouseEvent.MOUSE_RELEASED -> if (dragging) {
                        dragging = false
                    }
                }
            }
            Toolkit.getDefaultToolkit()
                .addAWTEventListener(listener, AWTEvent.MOUSE_EVENT_MASK or AWTEvent.MOUSE_MOTION_EVENT_MASK)
            awtListener = listener

            panel.setContent {
                Overlay(
                    window = window,
                    panel = panel,
                    scaleX = scaleX,
                    scaleY = scaleY,
                    onReady = { applyTransparentShape(window, it) },
                    onCloseClick = ::onCloseClick,
                )
            }

            window.contentPane.add(panel, BorderLayout.CENTER)
            window.pack()
            window.location = Point(INITIAL_WINDOW_X, INITIAL_WINDOW_Y)
            window.isVisible = true
            applyTransparentShape(window)
            KickOverlay.window = window
        }
    }

    public actual fun hide() {
        OverlaySettings.setEnabled(false)
        EventQueue.invokeLater {
            awtListener?.let { Toolkit.getDefaultToolkit().removeAWTEventListener(it) }
            awtListener = null
            window?.apply {
                isVisible = false
                dispose()
            }
            window = null
        }
    }

    private fun onCloseClick() = hide()

    private fun applyTransparentShape(
        w: JWindow,
        shapeDip: Dimension? = null,
        arc: Float = 16f
    ) {
        w.background = Color(0, 0, 0, 0)
        (w.contentPane as JComponent).apply {
            isOpaque = false
            background = Color(0, 0, 0, 0)
        }
        val sw = (shapeDip?.width ?: w.width).coerceIn(1, w.width)
        val sh = (shapeDip?.height ?: w.height).coerceIn(1, w.height)
        w.shape = java.awt.geom.RoundRectangle2D.Float(0f, 0f, sw.toFloat(), sh.toFloat(), arc, arc)
        try { w.rootPane.putClientProperty("apple.awt.windowShadow", false) } catch (_: Throwable) { }
    }
}
