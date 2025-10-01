package ru.bartwell.kick.module.sqlite.runtime.feature

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.module.sqlite.core.data.Column
import ru.bartwell.kick.module.sqlite.core.data.ColumnType
import ru.bartwell.kick.module.sqlite.core.data.Row
import ru.bartwell.kick.module.sqlite.runtime.feature.update.presentation.UpdateComponent
import ru.bartwell.kick.module.sqlite.runtime.feature.update.presentation.UpdateState
import ru.bartwell.kick.module.sqlite.runtime.feature.viewer.presentation.ViewerComponent
import ru.bartwell.kick.module.sqlite.runtime.feature.viewer.presentation.ViewerState

internal class FakeViewerComponent : ViewerComponent {
    private val _model = MutableValue(
        ViewerState(
            table = "items",
            columns = listOf(
                Column("_id", ColumnType.INTEGER, true, ""),
                Column("text", ColumnType.TEXT, false, null),
            ),
            rows = listOf(
                Row(ROW1, listOf("1", "A")),
                Row(ROW2, listOf("2", "B")),
                Row(ROW3, listOf("3", "C")),
            )
        )
    )
    override val model: Value<ViewerState> get() = _model

    var confirmInvoked = false
        private set
    override fun onBackPressed() = Unit
    override fun onStructureClick() = Unit
    override fun onInsertClick() = Unit
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

internal class FakeUpdateComponent : UpdateComponent {
    private val _model = MutableValue(
        UpdateState(
            table = "items",
            column = Column("text", ColumnType.TEXT, false, null),
            rowId = ROW1,
            value = "A",
            isNull = false,
        )
    )
    override val model: Value<UpdateState> get() = _model

    var saveInvoked = false
        private set
    override fun onBackPressed() = Unit
    override fun onValueChange(text: String) { _model.value = model.value.copy(value = text) }
    override fun onNullCheckboxClick() { _model.value = model.value.copy(isNull = !model.value.isNull) }
    override fun onSaveClick() { saveInvoked = true }
}

private const val ROW1: Long = 1L
private const val ROW2: Long = 2L
private const val ROW3: Long = 3L
