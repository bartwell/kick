package ru.bartwell.kick.module.overlay.core.overlay

import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.IntSize

@Composable
internal fun AutosizeMeasure(
    onDesiredSize: (IntSize) -> Unit,
    content: @Composable () -> Unit
) {
    SubcomposeLayout { constraints ->
        val desiredPlaceables = subcompose("desired", content).map {
            it.measure(
                constraints.copy(
                    minWidth = 0,
                    maxWidth = Int.MAX_VALUE,
                    minHeight = 0,
                    maxHeight = constraints.maxHeight
                )
            )
        }
        val desiredW = desiredPlaceables.maxOfOrNull { it.width } ?: 0
        val desiredH = desiredPlaceables.maxOfOrNull { it.height } ?: 0
        onDesiredSize(IntSize(desiredW, desiredH))

        val actualPlaceables = subcompose("actual", content).map { it.measure(constraints) }
        val w = actualPlaceables.maxOfOrNull { it.width } ?: constraints.minWidth
        val h = actualPlaceables.maxOfOrNull { it.height } ?: constraints.minHeight

        layout(w, h) { actualPlaceables.forEach { it.place(0, 0) } }
    }
}
