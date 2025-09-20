package ru.bartwell.kick.module.overlay.core.overlay

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
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
import javax.swing.SwingUtilities
import kotlin.math.max
import kotlin.math.roundToInt

public actual object KickOverlay {
    private var window: JWindow? = null
    private var awtListener: AWTEventListener? = null

    private const val MIN_W_DIP = 180
    private const val MIN_H_DIP = 60
    private const val HYSTERESIS_DIP = 2

    public actual fun init(context: PlatformContext) {

    }

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

            var dragging = false
            var pressOffsetX = 0
            var pressOffsetY = 0
            val listener = AWTEventListener { ev ->
                if (ev !is MouseEvent) return@AWTEventListener
                when (ev.id) {
                    MouseEvent.MOUSE_PRESSED -> {
                        val b = Rectangle(w.x, w.y, w.width, w.height)
                        if (b.contains(ev.xOnScreen, ev.yOnScreen)) {
                            dragging = true
                            pressOffsetX = ev.xOnScreen - w.x
                            pressOffsetY = ev.yOnScreen - w.y
                            w.toFront()
                        }
                    }
                    MouseEvent.MOUSE_DRAGGED -> if (dragging) {
                        w.setLocation(ev.xOnScreen - pressOffsetX, ev.yOnScreen - pressOffsetY)
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
                MaterialTheme {
                    var actualPx by remember { mutableStateOf(IntSize(360, 220)) }
                    var desiredPx by remember { mutableStateOf(IntSize(360, 220)) }

                    AutosizeMeasure(onDesiredSize = { desiredPx = it }) {
                        OverlayWindow(onCloseClick = { onCloseClicked() })
                    }

                    androidx.compose.foundation.layout.Box(
                        Modifier.onSizeChanged { actualPx = it }
                    )

                    LaunchedEffect(desiredPx, actualPx) {
                        SwingUtilities.invokeLater {
                            val dipWDesired = max(1, (desiredPx.width / scaleX).roundToInt())
                            val dipHDesired = max(1, (desiredPx.height / scaleY).roundToInt())

                            val contentDipW = max(1, (actualPx.width / scaleX).roundToInt())
                            val contentDipH = max(1, (actualPx.height / scaleY).roundToInt())
                            val contentDip = Dimension(contentDipW, contentDipH)

                            val targetW = max(MIN_W_DIP, dipWDesired)
                            val targetH = max(MIN_H_DIP, dipHDesired)
                            val target = Dimension(targetW, targetH)

                            val curW = w.width
                            val curH = w.height
                            val needResize =
                                kotlin.math.abs(target.width - curW) >= HYSTERESIS_DIP ||
                                        kotlin.math.abs(target.height - curH) >= HYSTERESIS_DIP

                            if (needResize) {
                                panel.preferredSize = target
                                panel.minimumSize = target
                                panel.maximumSize = target
                                w.pack()
                                if (w.size != target) w.size = target
                            }

                            val shapeDip = Dimension(
                                kotlin.math.min(w.width, contentDip.width),
                                kotlin.math.min(w.height, contentDip.height)
                            )
                            applyTransparentShape(w, shapeDip)
                        }
                    }
                }
            }

            w.contentPane.add(panel, BorderLayout.CENTER)
            w.pack()
            w.location = Point(50, 200)
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

    private fun onCloseClicked() = hide()

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
