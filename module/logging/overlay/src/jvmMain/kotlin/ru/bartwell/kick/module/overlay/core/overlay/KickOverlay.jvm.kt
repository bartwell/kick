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

    public actual fun show() {
        if (window != null) {
            window!!.isVisible = true
            window!!.toFront()
            return
        }
        OverlaySettings.setEnabled(true)

        EventQueue.invokeLater {
            val w = createWindow()
            val panel = createPanel()
            val (scaleX, scaleY) = computeScale(w)

            installDragListenerFor(w)
            setupOverlayContent(w, panel, scaleX, scaleY)
            attachAndShow(w, panel)
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

    private fun createWindow(): JWindow = JWindow().apply {
        isAlwaysOnTop = true
        background = Color(0, 0, 0, 0)
        layout = BorderLayout()
        (rootPane as JComponent).isOpaque = false
        (contentPane as JComponent).apply {
            isOpaque = false
            background = Color(0, 0, 0, 0)
        }
    }

    private fun createPanel(): ComposePanel = ComposePanel().apply {
        isOpaque = false
        background = Color(0, 0, 0, 0)
    }

    private fun computeScale(w: JWindow): Pair<Double, Double> {
        val tx = w.graphicsConfiguration?.defaultTransform
        return (tx?.scaleX ?: 1.0) to (tx?.scaleY ?: 1.0)
    }

    private fun installDragListenerFor(win: JWindow) {
        val controller = DragController(win)
        val listener = AWTEventListener { ev ->
            if (ev is MouseEvent) controller.onMouseEvent(ev)
        }
        Toolkit.getDefaultToolkit().addAWTEventListener(
            listener,
            AWTEvent.MOUSE_EVENT_MASK or AWTEvent.MOUSE_MOTION_EVENT_MASK
        )
        awtListener = listener
    }

    private fun setupOverlayContent(
        w: JWindow,
        panel: ComposePanel,
        scaleX: Double,
        scaleY: Double,
    ) {
        panel.setContent {
            Overlay(
                window = w,
                panel = panel,
                scaleX = scaleX,
                scaleY = scaleY,
                onReady = { dim -> applyTransparentShape(w, dim, arc = 0f) },
                onCloseClick = ::onCloseClick,
            )
        }
    }

    private fun attachAndShow(w: JWindow, panel: ComposePanel) {
        w.contentPane.add(panel, BorderLayout.CENTER)
        w.pack()
        w.location = Point(INITIAL_WINDOW_X, INITIAL_WINDOW_Y)
        w.isVisible = true
        applyTransparentShape(w, arc = 0f)
    }

    private fun applyTransparentShape(
        w: JWindow,
        shapeDip: Dimension? = null,
        arc: Float = 0f
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

        w.shape = if (arc <= 0f) {
            java.awt.geom.Rectangle2D.Float(
                0f,
                0f,
                proposedShapeWidthDip.toFloat(),
                proposedShapeHeightDip.toFloat(),
            )
        } else {
            java.awt.geom.RoundRectangle2D.Float(
                0f,
                0f,
                proposedShapeWidthDip.toFloat(),
                proposedShapeHeightDip.toFloat(),
                arc,
                arc
            )
        }
        try { w.rootPane.putClientProperty("apple.awt.windowShadow", false) } catch (_: Throwable) { }
    }

    private class DragController(private val win: JWindow) {
        private var pressed = false
        private var dragging = false
        private var pressOffsetX = 0
        private var pressOffsetY = 0
        private var pressStartX = 0
        private var pressStartY = 0

        fun onMouseEvent(ev: MouseEvent) {
            when (ev.id) {
                MouseEvent.MOUSE_PRESSED -> handlePressed(ev)
                MouseEvent.MOUSE_DRAGGED -> handleDragged(ev)
                MouseEvent.MOUSE_RELEASED -> handleReleased()
            }
        }

        private fun handlePressed(ev: MouseEvent) {
            if (ev.button != MouseEvent.BUTTON1) return
            if (!inWindowRect(ev.xOnScreen, ev.yOnScreen)) return
            if (!inWindowShape(ev.xOnScreen, ev.yOnScreen)) return

            pressed = true
            dragging = false
            pressStartX = ev.xOnScreen
            pressStartY = ev.yOnScreen
            pressOffsetX = ev.xOnScreen - win.x
            pressOffsetY = ev.yOnScreen - win.y
            win.toFront()
        }

        private fun handleDragged(ev: MouseEvent) {
            if (!pressed || ev.modifiersEx and MouseEvent.BUTTON1_DOWN_MASK == 0) return
            val dx = kotlin.math.abs(ev.xOnScreen - pressStartX)
            val dy = kotlin.math.abs(ev.yOnScreen - pressStartY)
            if (!dragging && (dx >= DRAG_SLOP || dy >= DRAG_SLOP)) dragging = true
            if (dragging) win.setLocation(ev.xOnScreen - pressOffsetX, ev.yOnScreen - pressOffsetY)
        }

        private fun handleReleased() {
            pressed = false
            dragging = false
        }

        private fun inWindowRect(x: Int, y: Int): Boolean =
            Rectangle(win.x, win.y, win.width, win.height).contains(x, y)

        private fun inWindowShape(x: Int, y: Int): Boolean {
            val localX = x - win.x
            val localY = y - win.y
            val shp = win.shape
            return shp?.contains(localX.toDouble(), localY.toDouble()) ?: true
        }
    }
}
