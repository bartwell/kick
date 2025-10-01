package ru.bartwell.kick.module.logging.feature.table.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.logging.core.persist.LogEntity
import ru.bartwell.kick.module.logging.core.persist.LoggingDatabase
import ru.bartwell.kick.module.logging.feature.table.util.LabelExtractor
import ru.bartwell.kick.module.logging.feature.table.util.LaunchUtils

internal class DefaultLogViewerComponent(
    componentContext: ComponentContext,
    private val database: LoggingDatabase,
    private val labelExtractor: LabelExtractor?,
    private val onFinished: () -> Unit,
) : LogViewerComponent, ComponentContext by componentContext {

    private val uiScope = coroutineScope()
    private val _model = MutableValue(LogViewerState())
    override val model: Value<LogViewerState> = _model
    private var job: Job? = null

    private var rawLogs: List<LogEntity> = emptyList()
    private var rawLabels: List<Set<String>> = emptyList()

    init {
        subscribeAll()
    }

    override fun onBackPressed() = onFinished()

    override fun onClearAllClick() {
        uiScope.launch { database.getLogDao().deleteAll() }
    }

    override fun onFilterClick() {
        val current = model.value
        if (current.isFilterActive) {
            _model.value = current.copy(isFilterActive = false, filterQuery = "")
        } else {
            _model.value = current.copy(isFilterDialogVisible = true, filterQuery = current.filterQuery)
        }
        filterAndUpdateLog()
    }

    override fun onFilterDialogDismiss() {
        _model.value = model.value.copy(isFilterDialogVisible = false)
    }

    override fun onFilterTextChange(text: String) {
        _model.value = model.value.copy(filterQuery = text)
    }

    override fun onFilterApply() {
        _model.value = model.value.copy(isFilterActive = true, isFilterDialogVisible = false)
        filterAndUpdateLog()
    }

    override fun onShareClick(context: PlatformContext) {
        LaunchUtils.shareLogs(context = context, logs = model.value.log)
    }

    override fun onLabelClick(label: String) {
        val selected = model.value.selectedLabels.toMutableSet()
        if (!selected.add(label)) {
            selected.remove(label)
        }
        _model.value = model.value.copy(selectedLabels = selected)
        filterAndUpdateLog()
    }

    private fun subscribeAll() {
        job?.cancel()
        job = database.getLogDao()
            .getAllAsFlow()
            .onEach { raw ->
                rawLogs = raw
                rawLabels = if (labelExtractor == null) {
                    List(raw.size) { emptySet() }
                } else {
                    raw.map { e -> labelExtractor.extract(e.message) }
                }
                filterAndUpdateLog()
            }
            .catch { _model.value = model.value.copy(error = it.toString()) }
            .launchIn(uiScope)
    }

    private fun filterAndUpdateLog() {
        val state = model.value
        val baseIndices: List<Int> = if (state.isFilterActive && state.filterQuery.isNotBlank()) {
            val query = state.filterQuery
            rawLogs.indices.filter { idx -> rawLogs[idx].message.contains(query, ignoreCase = true) }
        } else {
            rawLogs.indices.toList()
        }

        val allLabels = baseIndices.asSequence()
            .flatMap { idx -> rawLabels[idx].asSequence() }
            .distinct()
            .sorted()
            .toList()

        val selected = state.selectedLabels.intersect(allLabels.toSet())

        val visibleIndexes = if (selected.isEmpty()) {
            baseIndices
        } else {
            baseIndices.filter { index ->
                val labels = rawLabels[index]
                selected.all { it in labels }
            }
        }

        _model.value = state.copy(
            labels = allLabels,
            selectedLabels = selected,
            log = visibleIndexes.map { rawLogs[it] },
            error = null,
        )
    }
}
