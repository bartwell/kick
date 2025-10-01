package ru.bartwell.kick.module.sqlite.runtime.feature.insert.presentation

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.module.sqlite.core.data.Column
import ru.bartwell.kick.module.sqlite.core.data.ColumnType
import ru.bartwell.kick.module.sqlite.runtime.feature.insert.data.InsertValueType

internal class FakeInsertComponent : InsertComponent {
    private val columns = listOf(
        Column("_id", ColumnType.INTEGER, true, ""),
        Column("text", ColumnType.TEXT, false, null),
    )
    private val _model = MutableValue(
        InsertState(
            table = "items",
            columns = columns,
            values = emptyMap(),
            valueTypes = columns.associateWith { InsertValueType.VALUE },
        )
    )
    override val model: Value<InsertState> get() = _model

    var saveInvoked = false
        private set

    override fun onBackPressed() = Unit
    override fun onValueChange(column: Column, text: String) {
        _model.value = model.value.copy(values = model.value.values.toMutableMap().apply { put(column, text) })
    }
    override fun onValueTypeChange(column: Column, type: InsertValueType) {
        _model.value = model.value.copy(valueTypes = model.value.valueTypes.toMutableMap().apply { put(column, type) })
    }
    override fun onSaveClick() { saveInvoked = true }
    override fun onAlertDismiss() = Unit
}
