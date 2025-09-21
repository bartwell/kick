package ru.bartwell.kick.module.overlay.core.overlay

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.unit.IntSize
import java.awt.Dimension
import javax.swing.JWindow
import javax.swing.SwingUtilities
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

private const val MIN_W_DIP = 180
private const val MIN_H_DIP = 60
private const val HYSTERESIS_DIP = 2

@Composable
internal fun Overlay(
    window: JWindow,
    panel: ComposePanel,
    scaleX: Double,
    scaleY: Double,
    onReady: (Dimension) -> Unit,
    onCloseClick: () -> Unit,
) {
    MaterialTheme {
        var desiredPx by remember { mutableStateOf(IntSize(0, 0)) }
        var actualPx by remember { mutableStateOf(IntSize(0, 0)) }

        AutosizeMeasure(
            onSizes = { desired, actual ->
                desiredPx = desired
                actualPx = actual
            }
        ) {
            OverlayWindow(onCloseClick = onCloseClick)
        }

        LaunchedEffect(desiredPx, actualPx) {
            SwingUtilities.invokeLater {
                fun pxToDipW(px: Int) = max(1, (px / scaleX).roundToInt())
                fun pxToDipH(px: Int) = max(1, (px / scaleY).roundToInt())

                val dipWDesired = pxToDipW(desiredPx.width)
                val dipHDesired = pxToDipH(desiredPx.height)

                val targetW = max(MIN_W_DIP, dipWDesired)
                val targetH = max(MIN_H_DIP, dipHDesired)
                val target = Dimension(targetW, targetH)

                val needResize =
                    kotlin.math.abs(target.width - window.width) >= HYSTERESIS_DIP ||
                        kotlin.math.abs(target.height - window.height) >= HYSTERESIS_DIP

                if (needResize) {
                    panel.preferredSize = target
                    panel.minimumSize = target
                    panel.maximumSize = target
                    window.pack()
                    if (window.size != target) window.size = target
                }

                val dipWActual = pxToDipW(actualPx.width)
                val dipHActual = pxToDipH(actualPx.height)
                val contentDip = Dimension(
                    max(1, if (dipWActual > 0) dipWActual else target.width),
                    max(1, if (dipHActual > 0) dipHActual else target.height),
                )

                val shapeDip = Dimension(
                    min(window.width, contentDip.width),
                    min(window.height, contentDip.height)
                )
                onReady(shapeDip)
            }
        }
    }
}
