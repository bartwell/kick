package ru.bartwell.kick.module.ktor3.feature.list.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component

public interface RequestsListComponent : Component {
    public val model: Value<RequestsListState>

    public fun onBackPressed()
    public fun onClearAllClick()
    public fun onSearchClick()
    public fun onSearchDialogDismiss()
    public fun onSearchApply()
    public fun onSearchTextChange(text: String)
    public fun onItemClick(requestId: Long)
}
