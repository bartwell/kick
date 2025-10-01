package ru.bartwell.kick.module.overlay.core.overlay

import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.IntSize

@Composable
internal fun AutosizeMeasure(
    onSizes: (desired: IntSize, actual: IntSize) -> Unit,
    desiredContent: @Composable () -> Unit,
    actualContent: @Composable () -> Unit,
) {
    SubcomposeLayout { constraints ->
        val desiredPlaceables = subcompose("desired", desiredContent).map {
            it.measure(
                constraints.copy(
                    minWidth = 0,
                    maxWidth = Int.MAX_VALUE,
                    minHeight = 0,
                    maxHeight = Int.MAX_VALUE
                )
            )
        }
        val desiredW = desiredPlaceables.maxOfOrNull { it.width } ?: 0
        val desiredH = desiredPlaceables.sumOf { it.height }.coerceAtLeast(0)
        val desired = IntSize(desiredW, desiredH)

        val fixed = constraints.copy(
            minWidth = desiredW,
            maxWidth = desiredW,
            minHeight = desiredH,
            maxHeight = desiredH
        )
        val actualPlaceables = subcompose("actual", actualContent).map { it.measure(fixed) }
        val actual = IntSize(desiredW, desiredH)

        onSizes(desired, actual)

        layout(desiredW, desiredH) {
            actualPlaceables.forEach { it.place(0, 0) }
        }
    }
}
