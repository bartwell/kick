package ru.bartwell.kick.module.overlay.core.overlay

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import ru.bartwell.kick.module.overlay.core.overlay.KickOverlay.applyTransparentShape
import java.awt.Dimension
import javax.swing.JWindow
import javax.swing.SwingUtilities
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

private const val MIN_W_DIP = 180
private const val MIN_H_DIP = 60
private const val HYSTERESIS_DIP = 2
private const val INITIAL_X = 360
private const val INITIAL_Y = 220

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
        var actualPx by remember { mutableStateOf(IntSize(INITIAL_X, INITIAL_Y)) }
        var desiredPx by remember { mutableStateOf(IntSize(INITIAL_X, INITIAL_Y)) }

        AutosizeMeasure(onDesiredSize = { desiredPx = it }) {
            OverlayWindow(onCloseClick = { onCloseClick() })
        }

        Box(
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

                val curW = window.width
                val curH = window.height
                val needResize =
                    kotlin.math.abs(target.width - curW) >= HYSTERESIS_DIP ||
                            kotlin.math.abs(target.height - curH) >= HYSTERESIS_DIP

                if (needResize) {
                    panel.preferredSize = target
                    panel.minimumSize = target
                    panel.maximumSize = target
                    window.pack()
                    if (window.size != target) window.size = target
                }

                val shapeDip = Dimension(
                    min(window.width, contentDip.width),
                    min(window.height, contentDip.height)
                )
                onReady(shapeDip)
            }
        }
    }
}
