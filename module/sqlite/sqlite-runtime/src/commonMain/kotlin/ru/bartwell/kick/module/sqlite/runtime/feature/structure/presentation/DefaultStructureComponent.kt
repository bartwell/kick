package ru.bartwell.kick.feature.structure.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.bartwell.kick.core.DatabaseWrapper
import ru.bartwell.kick.core.mapper.ColumnsSqlMapper

internal class DefaultStructureComponent(
    componentContext: ComponentContext,
    databaseWrapper: DatabaseWrapper,
    table: String,
    private val onFinished: () -> Unit,
) : StructureComponent, ComponentContext by componentContext {

    private val _model = MutableValue(StructureState(table = table))
    override val model: Value<StructureState> = _model

    init {
        databaseWrapper
            .query("PRAGMA table_info($table);", ColumnsSqlMapper())
            .onEach { _model.value = _model.value.copy(columns = it) }
            .catch { _model.value = _model.value.copy(error = it.toString()) }
            .launchIn(coroutineScope())
    }

    override fun onBackPressed() = onFinished()
}
