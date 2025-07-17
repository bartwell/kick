package ru.bartwell.kick.feature.query.presentation

import ru.bartwell.kick.core.data.Row

public data class QueryState(
    val query: String = "",
    val result: List<List<String?>> = emptyList(),
    val message: String = "",
    val isError: Boolean = false,
) {
    val rows: List<Row>
        get() = result.map { Row(id = 0L, data = it) }
}
