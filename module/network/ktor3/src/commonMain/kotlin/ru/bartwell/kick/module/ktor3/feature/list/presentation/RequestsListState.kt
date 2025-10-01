package ru.bartwell.kick.module.ktor3.feature.list.presentation

import ru.bartwell.kick.module.ktor3.core.persist.RequestEntity

public data class RequestsListState(
    val requests: List<RequestEntity> = emptyList(),
    val error: String? = null,
    val searchQuery: String = "",
    val isSearchDialogVisible: Boolean = false,
)
