package ru.bartwell.kick.module.sqlite.runtime.feature.viewer.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component
import ru.bartwell.kick.module.sqlite.core.data.Column

public interface ViewerComponent : Component {
    public val model: Value<ViewerState>

    public fun onBackPressed()
    public fun onStructureClick()
    public fun onInsertClick()
    public fun onCellClick(column: Column, rowId: Long)
    public fun onDeleteClick()
    public fun onRowSelected(rowId: Long, isSelected: Boolean)
    public fun onCancelDeleteClick()
    public fun onConfirmDeleteClick()
    public fun onAlertDismiss()
}
