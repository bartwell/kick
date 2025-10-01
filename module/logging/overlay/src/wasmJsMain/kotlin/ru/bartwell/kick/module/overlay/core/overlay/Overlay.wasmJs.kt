package ru.bartwell.kick.module.overlay.core.overlay

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.IntSize
import org.w3c.dom.HTMLElement
import kotlin.math.max

@Composable
internal fun Overlay(
    root: HTMLElement,
    onCloseClick: () -> Unit,
) {
    MaterialTheme {
        var desiredPx by remember { mutableStateOf(IntSize(0, 0)) }

        AutosizeMeasure(
            onSizes = { desired, _ -> desiredPx = desired },
            desiredContent = { OverlayWindow(onCloseClick = onCloseClick, measureFull = true) },
            actualContent = { OverlayWindow(onCloseClick = onCloseClick, measureFull = false) }
        )

        LaunchedEffect(desiredPx) {
            root.style.width = "${max(1, desiredPx.width)}px"
            root.style.height = "${max(1, desiredPx.height)}px"
        }
    }
}
