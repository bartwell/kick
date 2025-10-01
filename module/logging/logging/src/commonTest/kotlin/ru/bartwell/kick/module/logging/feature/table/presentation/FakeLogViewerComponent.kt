package ru.bartwell.kick.module.logging.feature.table.presentation

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.logging.core.persist.LogEntity

internal class FakeLogViewerComponent(
    initial: List<LogEntity>,
) : LogViewerComponent {
    private val allLogs = initial.sortedByDescending { it.time }
    private val _model = MutableValue(
        LogViewerState(
            log = allLogs,
            isFilterActive = false,
            isFilterDialogVisible = false,
            filterQuery = "",
            error = null,
        )
    )
    override val model: Value<LogViewerState> get() = _model

    var shareInvoked: Boolean = false
        private set

    override fun onBackPressed() = Unit

    override fun onClearAllClick() {
        _model.value = model.value.copy(log = emptyList())
    }

    override fun onFilterClick() {
        if (model.value.isFilterActive) {
            _model.value = model.value.copy(isFilterActive = false, log = allLogs)
        } else {
            _model.value = model.value.copy(isFilterDialogVisible = true, filterQuery = "")
        }
    }

    override fun onFilterDialogDismiss() {
        _model.value = model.value.copy(isFilterDialogVisible = false)
    }

    override fun onFilterTextChange(text: String) {
        _model.value = model.value.copy(filterQuery = text)
    }

    override fun onFilterApply() {
        val q = model.value.filterQuery
        val filtered = allLogs.filter { it.message.contains(q) }
        _model.value = model.value.copy(
            isFilterActive = true,
            isFilterDialogVisible = false,
            log = filtered,
        )
    }

    override fun onShareClick(context: PlatformContext) {
        shareInvoked = true
    }
}
