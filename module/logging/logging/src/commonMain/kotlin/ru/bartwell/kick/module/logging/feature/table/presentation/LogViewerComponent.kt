package ru.bartwell.kick.module.logging.feature.table.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component
import ru.bartwell.kick.core.data.PlatformContext

public interface LogViewerComponent : Component {
    public val model: Value<LogViewerState>

    public fun onBackPressed()
    public fun onClearAllClick()
    public fun onFilterClick()
    public fun onFilterDialogDismiss()
    public fun onFilterApply()
    public fun onFilterTextChange(text: String)
    public fun onShareClick(context: PlatformContext)
}
