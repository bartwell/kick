package ru.bartwell.kick.module.ktor3.feature.detail.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.ktor3.core.persist.Ktor3Database
import ru.bartwell.kick.module.ktor3.feature.detail.extension.buildFullTransactionReport
import ru.bartwell.kick.module.ktor3.feature.detail.extension.copyToClipboard
import ru.bartwell.kick.module.ktor3.feature.list.data.Header

internal class DefaultRequestDetailsComponent(
    componentContext: ComponentContext,
    private val database: Ktor3Database,
    private val requestId: Long,
    private val onFinished: () -> Unit,
) : RequestDetailsComponent, ComponentContext by componentContext {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _model = MutableValue(RequestDetailsState())
    override val model: Value<RequestDetailsState> = _model

    init {
        loadData()
    }

    private fun loadData() {
        scope.launch(Dispatchers.Default) {
            try {
                val entity = database.getRequestDao().getById(requestId)
                _model.value = if (entity != null) {
                    _model.value.copy(
                        request = entity,
                        requestHeaders = parseHeaders(entity.requestHeaders),
                        requestBody = entity.requestBody,
                        responseHeaders = parseHeaders(entity.responseHeaders),
                        responseBody = entity.responseBody,
                        isLoading = false,
                    )
                } else {
                    _model.value.copy(isLoading = false)
                }
            } catch (_: Throwable) {
                _model.value = _model.value.copy(isLoading = false)
            }
        }
    }

    override fun onBackPressed() = onFinished()

    override fun onTabSelected(index: Int) {
        _model.value = model.value.copy(selectedTab = index)
    }

    override fun onCopyClick(context: PlatformContext) {
        val state = model.value
        val text = state.request?.buildFullTransactionReport(
            requestHeaders = state.requestHeaders,
            requestBody = state.requestBody,
            responseHeaders = state.responseHeaders,
            responseBody = state.responseBody,
            error = state.request.error,
        ) ?: return
        context.copyToClipboard(text)
    }

    private fun parseHeaders(headers: String?): List<Header> {
        if (headers.isNullOrBlank()) return emptyList()
        return headers.lines().mapNotNull { line ->
            val index = line.indexOf(":")
            if (index > 0) {
                Header(
                    key = line.substring(0, index).trim(),
                    value = line.substring(index + 1).trim()
                )
            } else {
                null
            }
        }
    }
}
