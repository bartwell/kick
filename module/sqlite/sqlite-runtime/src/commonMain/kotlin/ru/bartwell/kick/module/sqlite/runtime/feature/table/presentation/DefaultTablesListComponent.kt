package ru.bartwell.kick.feature.table.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.bartwell.kick.core.DatabaseWrapper
import ru.bartwell.kick.core.mapper.StringSqlMapper

internal class DefaultTablesListComponent(
    componentContext: ComponentContext,
    private val onFinished: () -> Unit,
    private val queryClicked: () -> Unit,
    private val listItemClicked: (String) -> Unit,
    databaseWrapper: DatabaseWrapper,
) : TablesListComponent, ComponentContext by componentContext {

    private val _model = MutableValue(TablesListState())
    override val model: Value<TablesListState> = _model

    init {
        val sql = "SELECT name FROM sqlite_master WHERE type='table' " +
            "AND name NOT IN ('sqlite_sequence', 'sqlite_stat1', " +
            "'sqlite_stat4', 'android_metadata', 'room_master_table');"
        databaseWrapper
            .query(sql, StringSqlMapper())
            .onEach { _model.value = _model.value.copy(tables = it) }
            .catch { _model.value = _model.value.copy(error = it.toString()) }
            .launchIn(coroutineScope())
    }

    override fun onBackPressed() = onFinished()
    override fun onQueryClick() = queryClicked()

    override fun onListItemClicked(table: String) = listItemClicked(table)
}
