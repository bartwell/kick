package ru.bartwell.kick.module.ktor3.feature.list.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.bartwell.kick.module.ktor3.core.persist.Ktor3Database

internal class DefaultRequestsListComponent(
    componentContext: ComponentContext,
    private val database: Ktor3Database,
    private val onFinished: () -> Unit,
    private val onRequestClick: (Long) -> Unit,
) : RequestsListComponent, ComponentContext by componentContext {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _model = MutableValue(RequestsListState())
    override val model: Value<RequestsListState> = _model

    init {
        loadData()
    }

    private fun loadData() {
        database.getRequestDao()
            .let { dao ->
                if (_model.value.searchQuery.isNotBlank()) {
                    dao.getFilteredAsFlow("%${_model.value.searchQuery}%")
                } else {
                    dao.getAllAsFlow()
                }
            }
            .onEach { _model.value = model.value.copy(requests = it, error = null) }
            .catch { _model.value = model.value.copy(error = it.toString()) }
            .launchIn(coroutineScope())
    }

    override fun onBackPressed() {
        onFinished()
    }

    override fun onClearAllClick() {
        scope.launch(Dispatchers.IO) {
            @Suppress("TooGenericExceptionCaught")
            try {
                database.getRequestDao().deleteAll()
            } catch (e: Exception) {
                _model.value = _model.value.copy(error = e.message)
            }
        }
    }

    override fun onSearchClick() {
        if (model.value.searchQuery.isBlank()) {
            _model.value = _model.value.copy(isSearchDialogVisible = true)
        } else {
            _model.value = _model.value.copy(searchQuery = "")
            loadData()
        }
    }

    override fun onSearchDialogDismiss() {
        _model.value = _model.value.copy(isSearchDialogVisible = false)
    }

    override fun onSearchTextChange(text: String) {
        _model.value = _model.value.copy(searchQuery = text)
    }

    override fun onSearchApply() {
        _model.value = _model.value.copy(isSearchDialogVisible = false)
        loadData()
    }

    override fun onItemClick(requestId: Long) {
        onRequestClick(requestId)
    }
}
