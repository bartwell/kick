package ru.bartwell.kick.core.ui

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties

@Composable
public fun ExposedDropdownMenuBox(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ExposedDropdownMenuBoxScope.() -> Unit,
) {
    val density = LocalDensity.current
    var anchorBounds by remember { mutableStateOf<Rect?>(null) }
    var anchorSize by remember { mutableStateOf<IntSize?>(null) }

    val scope = remember { ExposedDropdownMenuBoxScopeImpl() }
    scope.density = density
    scope.expanded = expanded
    scope.onExpandedChange = onExpandedChange
    scope.anchorBounds = anchorBounds
    scope.anchorSize = anchorSize
    scope.onAnchorPositioned = { coords ->
        anchorBounds = coords.boundsInWindow()
        anchorSize = coords.size
    }

    Box(modifier = modifier) {
        scope.content()
    }
}

public interface ExposedDropdownMenuBoxScope {
    public fun Modifier.menuAnchor(type: MenuAnchorType = MenuAnchorType.PrimaryNotEditable): Modifier

    @Composable
    public fun DropdownMenu(
        expanded: Boolean,
        onDismissRequest: () -> Unit,
        modifier: Modifier = Modifier,
        properties: PopupProperties = PopupProperties(focusable = true),
        content: @Composable ColumnScope.() -> Unit,
    )

    @Composable
    public fun ExposedDropdownMenu(
        expanded: Boolean,
        onDismissRequest: () -> Unit,
        modifier: Modifier = Modifier,
        properties: PopupProperties = PopupProperties(focusable = true),
        content: @Composable ColumnScope.() -> Unit,
    )
}

@Immutable
private class ExposedDropdownMenuBoxScopeImpl : ExposedDropdownMenuBoxScope {
    var expanded: Boolean = false
    lateinit var onExpandedChange: (Boolean) -> Unit
    var anchorBounds: Rect? = null
    var anchorSize: IntSize? = null
    var onAnchorPositioned: ((androidx.compose.ui.layout.LayoutCoordinates) -> Unit)? = null
    lateinit var density: androidx.compose.ui.unit.Density

    override fun Modifier.menuAnchor(type: MenuAnchorType): Modifier = composed {
        this
            .onGloballyPositioned { onAnchorPositioned?.invoke(it) }
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(
                        requireUnconsumed = false,
                        pass = PointerEventPass.Initial
                    )

                    var upHappened = false
                    while (true) {
                        val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break

                        if (change.changedToUp()) {
                            upHappened = true
                            break
                        }

                        if (!change.pressed) break
                    }

                    if (upHappened) {
                        onExpandedChange(!expanded)
                    }
                }
            }
    }

    @Composable
    override fun DropdownMenu(
        expanded: Boolean,
        onDismissRequest: () -> Unit,
        modifier: Modifier,
        properties: PopupProperties,
        content: @Composable ColumnScope.() -> Unit,
    ) {
        if (!expanded) return
        val widthDp = with(density) { (anchorSize?.width ?: 0).coerceAtLeast(0).toDp() }
        val appliedModifier = if (widthDp > 0.dp) modifier.width(widthDp) else modifier
        androidx.compose.material3.DropdownMenu(
            expanded = true,
            onDismissRequest = onDismissRequest,
            modifier = appliedModifier,
            properties = properties,
            content = content,
        )
    }

    @Composable
    override fun ExposedDropdownMenu(
        expanded: Boolean,
        onDismissRequest: () -> Unit,
        modifier: Modifier,
        properties: PopupProperties,
        content: @Composable ColumnScope.() -> Unit,
    ) {
        DropdownMenu(expanded, onDismissRequest, modifier, properties, content)
    }
}
