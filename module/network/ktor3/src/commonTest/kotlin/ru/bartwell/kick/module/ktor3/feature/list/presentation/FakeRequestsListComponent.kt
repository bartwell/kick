package ru.bartwell.kick.module.ktor3.feature.list.presentation

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.module.ktor3.core.persist.RequestEntity

internal class FakeRequestsListComponent(initial: List<RequestEntity>) : RequestsListComponent {
    private val all = initial.sortedByDescending { it.timestamp }
    private val _model = MutableValue(
        RequestsListState(
            requests = all,
            error = null,
            searchQuery = "",
            isSearchDialogVisible = false,
        )
    )
    override val model: Value<RequestsListState> get() = _model

    override fun onBackPressed() = Unit

    override fun onClearAllClick() {
        _model.value = model.value.copy(requests = emptyList())
    }

    override fun onSearchClick() {
        if (model.value.searchQuery.isBlank()) {
            _model.value = model.value.copy(isSearchDialogVisible = true)
        } else {
            _model.value = model.value.copy(searchQuery = "", requests = all)
        }
    }

    override fun onSearchDialogDismiss() {
        _model.value = model.value.copy(isSearchDialogVisible = false)
    }

    override fun onSearchApply() {
        _model.value = model.value.copy(isSearchDialogVisible = false)
        val q = model.value.searchQuery
        val filtered = all.filter { re ->
            re.url.contains(q) ||
                re.method.name.contains(q, ignoreCase = true) ||
                re.requestHeaders?.contains(q) == true ||
                re.requestBody?.contains(q) == true ||
                re.responseHeaders?.contains(q) == true ||
                re.responseBody?.contains(q) == true
        }
        _model.value = model.value.copy(requests = filtered)
    }

    override fun onSearchTextChange(text: String) {
        _model.value = model.value.copy(searchQuery = text)
    }

    override fun onItemClick(requestId: Long) = Unit
}
