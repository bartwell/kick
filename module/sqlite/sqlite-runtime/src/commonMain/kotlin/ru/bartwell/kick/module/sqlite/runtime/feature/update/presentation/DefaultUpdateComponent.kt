package ru.bartwell.kick.module.sqlite.runtime.feature.update.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.bartwell.kick.module.sqlite.core.DatabaseWrapper
import ru.bartwell.kick.module.sqlite.core.data.Column
import ru.bartwell.kick.module.sqlite.core.mapper.SingleStringSqlMapper

internal class DefaultUpdateComponent(
    componentContext: ComponentContext,
    private val databaseWrapper: DatabaseWrapper,
    table: String,
    column: Column,
    rowId: Long,
    private val onFinished: () -> Unit,
) : UpdateComponent, ComponentContext by componentContext {

    private val _model = MutableValue(
        UpdateState(
            table = table,
            column = column,
            rowId = rowId,
        )
    )
    override val model: Value<UpdateState> = _model

    init {
        updateData()
    }

    private fun updateData() {
        val table = model.value.table
        val columnName = model.value.column.name
        val rowId = model.value.rowId
        val rowIdColumn = Column.ROW_ID_COLUMN.name
        databaseWrapper
            .querySingle(
                "SELECT $columnName FROM $table WHERE $rowIdColumn = $rowId;",
                SingleStringSqlMapper(model.value.column)
            )
            .onEach { value ->
                if (value == null) {
                    _model.value = _model.value.copy(isNull = true)
                } else {
                    _model.value = _model.value.copy(value = value)
                }
            }
            .catch { _model.value = _model.value.copy(loadError = it.toString()) }
            .launchIn(coroutineScope())
    }

    override fun onBackPressed() = onFinished()

    override fun onValueChange(text: String) {
        _model.value = _model.value.copy(value = text)
    }

    override fun onNullCheckboxClick() {
        _model.value = _model.value.copy(isNull = !model.value.isNull)
    }

    override fun onSaveClick() {
        val newValue = model.value.value.takeIf { !model.value.isNull }
        databaseWrapper
            .updateSingle(model.value.table, model.value.rowId, model.value.column, newValue)
            .onEach {
                onBackPressed()
            }
            .catch { _model.value = _model.value.copy(saveError = it.toString()) }
            .launchIn(coroutineScope())
    }
}
