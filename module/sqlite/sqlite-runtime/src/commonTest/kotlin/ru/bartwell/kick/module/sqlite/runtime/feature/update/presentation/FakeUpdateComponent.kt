package ru.bartwell.kick.module.sqlite.runtime.feature.update.presentation

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.module.sqlite.core.data.Column
import ru.bartwell.kick.module.sqlite.core.data.ColumnType

internal class FakeUpdateComponent(
    table: String = "items",
    column: Column = Column("text", ColumnType.TEXT, isNotNullable = false, defaultValue = null),
    rowId: Long = 1,
    value: String? = "A",
    isNull: Boolean = false,
) : UpdateComponent {
    private val _model = MutableValue(
        UpdateState(
            table = table,
            column = column,
            rowId = rowId,
            value = value,
            isNull = isNull,
        )
    )
    override val model: Value<UpdateState> get() = _model

    var backInvoked = false
        private set
    var saveInvoked = false
        private set

    override fun onBackPressed() { backInvoked = true }
    override fun onValueChange(text: String) { _model.value = model.value.copy(value = text) }
    override fun onNullCheckboxClick() { _model.value = model.value.copy(isNull = !model.value.isNull) }
    override fun onSaveClick() { saveInvoked = true }
}
