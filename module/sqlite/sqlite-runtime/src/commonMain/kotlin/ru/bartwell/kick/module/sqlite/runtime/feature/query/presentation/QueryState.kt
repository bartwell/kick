package ru.bartwell.kick.module.sqlite.runtime.feature.query.presentation

import ru.bartwell.kick.module.sqlite.core.data.Row

public data class QueryState(
    val query: String = "",
    val result: List<List<String?>> = emptyList(),
    val message: String = "",
    val isError: Boolean = false,
) {
    val rows: List<Row>
        get() = result.map { Row(id = 0L, data = it) }
}
