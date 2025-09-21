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
private const val DRAG_SLOP = 6
private const val MIN_SHAPE_DIP = 8

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
            val w = JWindow().apply {
                isAlwaysOnTop = true
                background = Color(0, 0, 0, 0)
                layout = BorderLayout()
            }
            (w.rootPane as JComponent).isOpaque = false
            (w.contentPane as JComponent).apply {
                isOpaque = false
                background = Color(0, 0, 0, 0)
            }

            val panel = ComposePanel().apply {
                isOpaque = false
                background = Color(0, 0, 0, 0)
            }

            val tx = w.graphicsConfiguration?.defaultTransform
            val scaleX = tx?.scaleX ?: 1.0
            val scaleY = tx?.scaleY ?: 1.0

            var pressed = false
            var dragging = false
            var pressOffsetX = 0
            var pressOffsetY = 0
            var pressStartX = 0
            var pressStartY = 0

            val listener = AWTEventListener { ev ->
                if (ev !is MouseEvent) return@AWTEventListener
                val win = window ?: return@AWTEventListener

                fun inWindowRect(x: Int, y: Int): Boolean {
                    return Rectangle(win.x, win.y, win.width, win.height).contains(x, y)
                }
                fun inWindowShape(x: Int, y: Int): Boolean {
                    val localX = x - win.x
                    val localY = y - win.y
                    val shp = win.shape
                    return shp?.contains(localX.toDouble(), localY.toDouble()) ?: true
                }

                when (ev.id) {
                    MouseEvent.MOUSE_PRESSED -> {
                        if (ev.button == MouseEvent.BUTTON1 &&
                            inWindowRect(ev.xOnScreen, ev.yOnScreen) &&
                            inWindowShape(ev.xOnScreen, ev.yOnScreen)
                        ) {
                            pressed = true
                            dragging = false
                            pressStartX = ev.xOnScreen
                            pressStartY = ev.yOnScreen
                            pressOffsetX = ev.xOnScreen - win.x
                            pressOffsetY = ev.yOnScreen - win.y
                            win.toFront()
                        }
                    }
                    MouseEvent.MOUSE_DRAGGED -> {
                        if (pressed && ev.modifiersEx and MouseEvent.BUTTON1_DOWN_MASK != 0) {
                            val dx = kotlin.math.abs(ev.xOnScreen - pressStartX)
                            val dy = kotlin.math.abs(ev.yOnScreen - pressStartY)
                            if (!dragging && (dx >= DRAG_SLOP || dy >= DRAG_SLOP)) dragging = true
                            if (dragging) {
                                win.setLocation(ev.xOnScreen - pressOffsetX, ev.yOnScreen - pressOffsetY)
                            }
                        }
                    }
                    MouseEvent.MOUSE_RELEASED -> {
                        pressed = false
                        dragging = false
                    }
                }
            }
            Toolkit.getDefaultToolkit()
                .addAWTEventListener(
                    listener,
                    AWTEvent.MOUSE_EVENT_MASK or AWTEvent.MOUSE_MOTION_EVENT_MASK
                )
            awtListener = listener

            panel.setContent {
                Overlay(
                    window = w,
                    panel = panel,
                    scaleX = scaleX,
                    scaleY = scaleY,
                    onReady = { dim -> applyTransparentShape(w, dim) },
                    onCloseClick = ::onCloseClick,
                )
            }

            w.contentPane.add(panel, BorderLayout.CENTER)
            w.pack()
            w.location = Point(INITIAL_WINDOW_X, INITIAL_WINDOW_Y)
            w.isVisible = true
            applyTransparentShape(w)
            window = w
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

    private fun onCloseClick() {
        hide()
    }

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

        val swRaw = (shapeDip?.width ?: w.width).coerceIn(1, w.width)
        val shRaw = (shapeDip?.height ?: w.height).coerceIn(1, w.height)
        val proposedShapeWidthDip = if (swRaw < MIN_SHAPE_DIP) w.width else swRaw
        val proposedShapeHeightDip = if (shRaw < MIN_SHAPE_DIP) w.height else shRaw

        w.shape = java.awt.geom.RoundRectangle2D.Float(
            0f,
            0f,
            proposedShapeWidthDip.toFloat(),
            proposedShapeHeightDip.toFloat(),
            arc,
            arc
        )
        try { w.rootPane.putClientProperty("apple.awt.windowShadow", false) } catch (_: Throwable) { }
    }
}
