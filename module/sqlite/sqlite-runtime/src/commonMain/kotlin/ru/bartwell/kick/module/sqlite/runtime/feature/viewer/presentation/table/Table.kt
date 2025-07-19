package ru.bartwell.kick.module.sqlite.runtime.feature.viewer.presentation.table

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import ru.bartwell.kick.core.extension.orNull
import ru.bartwell.kick.module.sqlite.core.data.Column
import ru.bartwell.kick.module.sqlite.core.data.Row
import ru.bartwell.kick.module.sqlite.runtime.feature.viewer.extension.removeBuiltIn

private val MAX_CELL_WIDTH = 200.dp
private val CELL_PADDING = 8.dp
private val FIELD_TYPE_STYLE: TextStyle
    @Composable get() = MaterialTheme.typography.bodySmall

@Composable
internal fun Table(
    rows: List<Row>,
    columns: List<Column>? = null,
    isInSelectionMode: Boolean = false,
    selectedRows: List<Long> = emptyList(),
    onCellClick: ((column: Column, rowId: Long) -> Unit)? = null,
    onRowSelected: (rowId: Long, isSelected: Boolean) -> Unit = { _, _ -> },
) {
    val horizontalScrollState = rememberScrollState()
    val cellsWidths = calculateCellsWidths(columns, rows)
    val visibleColumns = columns?.removeBuiltIn()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .horizontalScroll(horizontalScrollState)
            .padding(16.dp),
    ) {
        visibleColumns?.let {
            item {
                TableCaption(
                    visibleColumns = visibleColumns,
                    cellsWidths = cellsWidths,
                    isInSelectionMode = isInSelectionMode,
                )
            }
        }
        items(rows.size) { index ->
            TableRow(
                row = rows[index],
                cellsWidths = cellsWidths,
                isCellClickable = onCellClick != null,
                isInSelectionMode = isInSelectionMode,
                selectedRows = selectedRows,
                onRowSelected = onRowSelected,
                onCellClick = { cellIndex, rowId ->
                    if (onCellClick != null && visibleColumns != null) {
                        onCellClick(visibleColumns[cellIndex], rowId)
                    }
                },
            )
        }
    }
}

@Composable
private fun TableRow(
    row: Row,
    cellsWidths: List<Dp>,
    isCellClickable: Boolean,
    isInSelectionMode: Boolean,
    selectedRows: List<Long>,
    onRowSelected: (Long, Boolean) -> Unit,
    onCellClick: ((Int, Long) -> Unit)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        if (isInSelectionMode) {
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .fillMaxHeight()
                    .border(1.dp, MaterialTheme.colorScheme.onBackground)
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                val isChecked = selectedRows.contains(row.id)
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { onRowSelected(row.id, it) },
                )
            }
        }
        for (cell in row.data.withIndex()) {
            TableCell(
                title = cell.value.orNull(),
                isClickable = isCellClickable,
                onClick = { onCellClick(cell.index, row.id) },
                width = cellsWidths[cell.index],
                textColor = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

@Composable
private fun TableCaption(visibleColumns: List<Column>, cellsWidths: List<Dp>, isInSelectionMode: Boolean) {
    Row(Modifier.background(MaterialTheme.colorScheme.primaryContainer)) {
        if (isInSelectionMode) {
            TableCell(
                title = "",
                subtitle = "",
                width = 48.dp,
                textColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        for ((columnIndex, column) in visibleColumns.withIndex()) {
            TableCell(
                title = column.name,
                subtitle = column.type.name.lowercase(),
                width = cellsWidths[columnIndex],
                textColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun TableCell(
    title: String,
    width: Dp,
    textColor: Color,
    isClickable: Boolean = false,
    onClick: () -> Unit = {},
    subtitle: String? = null
) {
    Box(
        modifier = Modifier
            .width(width)
            .fillMaxHeight()
            .border(1.dp, MaterialTheme.colorScheme.onBackground)
            .clickable(enabled = isClickable, onClick = onClick),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(CELL_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            subtitle?.let {
                Text(
                    text = subtitle,
                    color = textColor,
                    style = FIELD_TYPE_STYLE,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun calculateCellsWidths(columns: List<Column>?, rows: List<Row>): List<Dp> {
    val columnCount = columns?.size
        ?: rows.firstOrNull()?.data?.size
        ?: 0

    val initialWidths: List<Dp> = columns
        ?.map { column ->
            max(
                column.name.calculateTextWidth(),
                column.type.name
                    .lowercase()
                    .calculateTextWidth(FIELD_TYPE_STYLE)
            )
        }
        ?: List(columnCount) { 0.dp }

    val resultWidths = rows.fold(initialWidths) { acc, row ->
        acc.mapIndexed { index, currentMax ->
            val text = row.data.getOrNull(index).orNull()
            max(currentMax, text.calculateTextWidth())
        }
    }

    return resultWidths
}

@Composable
private fun String.calculateTextWidth(style: TextStyle = MaterialTheme.typography.bodyMedium): Dp {
    val textMeasurer = rememberTextMeasurer()
    val result = textMeasurer.measure(
        text = AnnotatedString(text = this),
        style = style,
    )
    val calculatedWidth = with(LocalDensity.current) { result.size.width.toDp() }
    val widthWithPaddings = calculatedWidth + CELL_PADDING * 2
    return min(widthWithPaddings, MAX_CELL_WIDTH)
}
