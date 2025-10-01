package ru.bartwell.kick.module.logging.feature.table.presentation

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.logging.core.persist.LogEntity

internal class FakeLogViewerComponent(
    initial: List<LogEntity>,
) : LogViewerComponent {
    private val allLogs = initial.sortedByDescending { it.time }
    private val regex = Regex("\\[(.*?)]")
    private val _model = MutableValue(
        LogViewerState(
            log = allLogs,
            isFilterActive = false,
            isFilterDialogVisible = false,
            filterQuery = "",
            error = null,
            labels = initial.flatMap { regex.findAll(it.message).map { m -> m.groupValues[1] } }
                .distinct()
                .sorted(),
            selectedLabels = emptySet(),
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
            _model.value = model.value.copy(isFilterActive = false)
            recalc()
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
        _model.value = model.value.copy(
            isFilterActive = true,
            isFilterDialogVisible = false,
        )
        recalc()
    }

    override fun onShareClick(context: PlatformContext) {
        shareInvoked = true
    }

    override fun onLabelClick(label: String) {
        val current = model.value.selectedLabels.toMutableSet()
        if (current.contains(label)) current.remove(label) else current.add(label)
        _model.value = model.value.copy(selectedLabels = current)
        recalc()
    }

    private fun recalc() {
        val textFiltered = if (model.value.isFilterActive) {
            val q = model.value.filterQuery
            allLogs.filter { it.message.contains(q) }
        } else {
            allLogs
        }
        val labels = textFiltered.flatMap { regex.findAll(it.message).map { m -> m.groupValues[1] } }
            .distinct()
            .sorted()
        val selected = model.value.selectedLabels.filter { it in labels }.toSet()
        val result = if (selected.isEmpty()) {
            textFiltered
        } else {
            textFiltered.filter { log ->
                val tags = regex.findAll(log.message).map { it.groupValues[1] }.toSet()
                selected.all { it in tags }
            }
        }
        _model.value = model.value.copy(
            log = result,
            labels = labels,
            selectedLabels = selected,
        )
    }
}
