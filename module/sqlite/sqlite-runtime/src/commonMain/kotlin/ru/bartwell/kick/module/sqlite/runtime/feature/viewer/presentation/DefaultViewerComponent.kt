package ru.bartwell.kick.module.sqlite.runtime.feature.viewer.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.bartwell.kick.core.component.Resumable
import ru.bartwell.kick.module.sqlite.core.DatabaseWrapper
import ru.bartwell.kick.module.sqlite.core.data.Column
import ru.bartwell.kick.module.sqlite.core.mapper.ColumnsSqlMapper
import ru.bartwell.kick.module.sqlite.core.mapper.RowsSqlMapper
import ru.bartwell.kick.module.sqlite.runtime.feature.viewer.extension.removeBuiltIn

internal class DefaultViewerComponent(
    componentContext: ComponentContext,
    private val databaseWrapper: DatabaseWrapper,
    table: String,
    private val callback: ViewerComponentCallback,
) : ViewerComponent, ComponentContext by componentContext, Resumable {

    private val _model = MutableValue(ViewerState(table = table))
    override val model: Value<ViewerState> = _model

    override fun onResume() {
        updateTable()
    }

    private fun updateTable() {
        loadColumns()
    }

    private fun loadColumns() {
        val table = model.value.table
        databaseWrapper
            .query("PRAGMA table_info($table);", ColumnsSqlMapper())
            .onEach { columns ->
                _model.value = _model.value.copy(columns = columns + listOf(Column.ROW_ID_COLUMN))
                loadRows()
            }
            .catch { _model.value = _model.value.copy(loadError = it.toString()) }
            .launchIn(coroutineScope())
    }

    private fun loadRows() {
        val table = model.value.table
        val columns = model.value.columns.joinToString(",") { it.name }
        databaseWrapper
            .query("SELECT $columns FROM $table;", RowsSqlMapper(model.value.columns))
            .onEach { _model.value = _model.value.copy(rows = it) }
            .catch { _model.value = _model.value.copy(loadError = it.toString()) }
            .launchIn(coroutineScope())
    }

    override fun onDeleteClick() {
        _model.value = model.value.copy(
            isDeleteMode = !model.value.isDeleteMode,
            selectedRows = if (model.value.isDeleteMode) emptyList() else model.value.selectedRows,
        )
    }

    override fun onRowSelected(rowId: Long, isSelected: Boolean) {
        val newSelected = if (isSelected) {
            model.value.selectedRows + rowId
        } else {
            model.value.selectedRows - rowId
        }
        _model.value = model.value.copy(selectedRows = newSelected)
    }

    override fun onBackPressed() = callback.onFinished()

    override fun onCancelDeleteClick() {
        _model.value = model.value.copy(isDeleteMode = false)
    }

    override fun onConfirmDeleteClick() {
        databaseWrapper
            .delete(model.value.table, model.value.selectedRows)
            .onEach {
                updateTable()
                _model.value = model.value.copy(isDeleteMode = false)
            }
            .catch { _model.value = _model.value.copy(deleteError = it.toString()) }
            .launchIn(coroutineScope())
    }

    override fun onStructureClick() = callback.structureClick(model.value.table)

    override fun onInsertClick() = callback.insertClick(model.value.table, model.value.columns.removeBuiltIn())

    override fun onCellClick(column: Column, rowId: Long) = callback.cellClick(model.value.table, column, rowId)

    override fun onAlertDismiss() {
        _model.value = model.value.copy(deleteError = null)
    }
}
