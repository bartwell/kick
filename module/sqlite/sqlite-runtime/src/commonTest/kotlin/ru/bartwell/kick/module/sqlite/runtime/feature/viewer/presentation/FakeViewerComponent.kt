package ru.bartwell.kick.module.sqlite.runtime.feature.viewer.presentation

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.module.sqlite.core.data.Column
import ru.bartwell.kick.module.sqlite.core.data.ColumnType
import ru.bartwell.kick.module.sqlite.core.data.Row

internal class FakeViewerComponent(
    table: String = "items",
    columns: List<Column> = listOf(
        Column("_id", ColumnType.INTEGER, isNotNullable = true, defaultValue = ""),
        Column("text", ColumnType.TEXT, isNotNullable = false, defaultValue = null),
    ),
    rows: List<Row> = listOf(
        Row(1, listOf("1", "A")),
        Row(2, listOf("2", "B")),
        Row(3, listOf("3", "C")),
    ),
) : ViewerComponent {
    private val _model = MutableValue(
        ViewerState(
            table = table,
            columns = columns,
            rows = rows,
            isDeleteMode = false,
            selectedRows = emptyList(),
        )
    )
    override val model: Value<ViewerState> get() = _model

    var backInvoked = false
        private set
    var structureInvoked = false
        private set
    var insertInvoked = false
        private set
    var confirmInvoked = false
        private set

    override fun onBackPressed() { backInvoked = true }
    override fun onStructureClick() { structureInvoked = true }
    override fun onInsertClick() { insertInvoked = true }
    override fun onCellClick(column: Column, rowId: Long) = Unit
    override fun onDeleteClick() { _model.value = model.value.copy(isDeleteMode = true) }
    override fun onRowSelected(rowId: Long, isSelected: Boolean) {
        val cur = model.value.selectedRows.toMutableSet()
        if (isSelected) cur.add(rowId) else cur.remove(rowId)
        _model.value = model.value.copy(selectedRows = cur.toList())
    }
    override fun onCancelDeleteClick() {
        _model.value = model.value.copy(
            isDeleteMode = false,
            selectedRows = emptyList(),
        )
    }
    override fun onConfirmDeleteClick() { confirmInvoked = true }
    override fun onAlertDismiss() = Unit
}
