package ru.bartwell.kick.module.sqlite.runtime.feature.query.presentation

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

internal class FakeQueryComponent : QueryComponent {
    private val _model = MutableValue(QueryState())
    override val model: Value<QueryState> get() = _model

    override fun onBackPressed() = Unit
    override fun onQueryChange(text: String) { _model.value = model.value.copy(query = text) }
    override fun onExecuteClick() {
        val q = model.value.query
        if (q.contains("error", ignoreCase = true)) {
            _model.value = model.value.copy(
                message = "Syntax error",
                isError = true,
                result = emptyList(),
            )
        } else {
            _model.value = model.value.copy(
                message = "",
                isError = false,
                result = listOf(
                    listOf("1", "A"),
                    listOf("2", "B"),
                ),
            )
        }
    }
    override fun onAlertDismiss() = Unit
}
