package ru.bartwell.kick.module.sqlite.runtime.feature.query.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.bartwell.kick.module.sqlite.core.DatabaseWrapper
import ru.bartwell.kick.module.sqlite.runtime.feature.query.mapper.QuerySqlMapper
import ru.bartwell.kick.module.sqlite.runtime.feature.query.util.SqlUtils

internal class DefaultQueryComponent(
    componentContext: ComponentContext,
    private val databaseWrapper: DatabaseWrapper,
    private val onFinished: () -> Unit,
) : QueryComponent, ComponentContext by componentContext {

    private val _model = MutableValue(QueryState())
    override val model: Value<QueryState> = _model

    override fun onBackPressed() = onFinished()

    override fun onQueryChange(text: String) {
        _model.value = _model.value.copy(query = text)
    }

    override fun onExecuteClick() {
        val query = model.value.query
        if (SqlUtils.mayReturnRows(query)) {
            databaseWrapper
                .query(query, mapper = QuerySqlMapper())
                .onEach { result: List<List<String?>> ->
                    if (result.isNotEmpty() && result.first().isNotEmpty()) {
                        _model.value = _model.value.copy(result = result.alignRows(), message = "")
                    } else {
                        _model.value = _model.value.copy(message = "Query returns empty result", isError = false)
                    }
                }
                .catch { _model.value = _model.value.copy(message = it.toString(), isError = true) }
                .launchIn(coroutineScope())
        } else {
            databaseWrapper
                .raw(query)
                .onEach {
                    _model.value = _model.value.copy(message = "Query executed without result", isError = false)
                }
                .catch { _model.value = _model.value.copy(message = it.toString(), isError = true) }
                .launchIn(coroutineScope())
        }
    }

    override fun onAlertDismiss() {
        _model.value = _model.value.copy(message = "")
    }
}

// Refer to QuerySqlMapper for details on why this extension
// is necessary and how it works
private fun List<List<String?>>.alignRows(): List<List<String?>> {
    // Determine the maximum number of columns in any row
    val maxCols = this.maxOfOrNull { it.size } ?: return emptyList()
    return this.map { row ->
        if (row.size < maxCols) {
            // Pad shorter rows with nulls
            row + List(maxCols - row.size) { null }
        } else {
            // Leave rows at max length unchanged
            row
        }
    }
}
