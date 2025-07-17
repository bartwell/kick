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
import ru.bartwell.kick.module.logging.core.persist.LoggingDatabase
import ru.bartwell.kick.module.logging.feature.table.util.LaunchUtils

internal class DefaultLogViewerComponent(
    componentContext: ComponentContext,
    private val onFinished: () -> Unit,
    private val database: LoggingDatabase,
) : LogViewerComponent, ComponentContext by componentContext {

    private val _model = MutableValue(LogViewerState())
    override val model: Value<LogViewerState> = _model
    private var job: Job? = null

    init {
        loadLog()
    }

    override fun onBackPressed() = onFinished()

    override fun onClearAllClick() {
        coroutineScope().launch {
            database.getLogDao().deleteAll()
        }
    }

    override fun onFilterClick() {
        if (model.value.isFilterActive) {
            _model.value = model.value.copy(isFilterActive = false)
            loadLog()
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
        _model.value = model.value.copy(isFilterActive = true, isFilterDialogVisible = false)
        loadLog()
    }

    override fun onShareClick(context: PlatformContext) {
        LaunchUtils.shareLogs(context = context, logs = model.value.log)
    }

    private fun loadLog() {
        job?.cancel()
        job = database.getLogDao()
            .let {
                if (model.value.isFilterActive) {
                    it.getFilteredAsFlow("%" + model.value.filterQuery + "%")
                } else {
                    it.getAllAsFlow()
                }
            }
            .onEach { _model.value = model.value.copy(log = it, error = null) }
            .catch { _model.value = model.value.copy(error = it.toString()) }
            .launchIn(coroutineScope())
    }
}
