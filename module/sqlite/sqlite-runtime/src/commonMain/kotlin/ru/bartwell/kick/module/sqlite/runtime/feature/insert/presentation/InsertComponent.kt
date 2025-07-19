package ru.bartwell.kick.module.sqlite.runtime.feature.insert.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component
import ru.bartwell.kick.module.sqlite.core.data.Column
import ru.bartwell.kick.module.sqlite.runtime.feature.insert.data.InsertValueType

public interface InsertComponent : Component {
    public val model: Value<InsertState>

    public fun onBackPressed()
    public fun onValueChange(column: Column, text: String)
    public fun onValueTypeChange(column: Column, type: InsertValueType)
    public fun onSaveClick()
    public fun onAlertDismiss()
}
