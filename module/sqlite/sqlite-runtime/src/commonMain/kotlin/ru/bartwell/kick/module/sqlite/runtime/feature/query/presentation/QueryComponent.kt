package ru.bartwell.kick.module.sqlite.runtime.feature.query.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component

public interface QueryComponent : Component {
    public val model: Value<QueryState>

    public fun onBackPressed()
    public fun onQueryChange(text: String)
    public fun onExecuteClick()
    public fun onAlertDismiss()
}
