package ru.bartwell.kick.module.runner.feature.list.presentation

internal data class RunnerListState(
    val calls: List<RunnerListItem> = emptyList(),
    val runningCallId: String? = null,
    val error: String? = null,
)

internal data class RunnerListItem(
    val id: String,
    val title: String,
    val description: String?,
)
