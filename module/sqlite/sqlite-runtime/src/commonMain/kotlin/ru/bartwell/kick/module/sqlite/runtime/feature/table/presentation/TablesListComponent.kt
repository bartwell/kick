package ru.bartwell.kick.module.sqlite.runtime.feature.table.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component

public interface TablesListComponent : Component {
    public val model: Value<TablesListState>

    public fun onBackPressed()
    public fun onQueryClick()
    public fun onListItemClicked(table: String)
}
