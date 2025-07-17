package ru.bartwell.kick.feature.update.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component

public interface UpdateComponent : Component {
    public val model: Value<UpdateState>

    public fun onBackPressed()
    public fun onValueChange(text: String)
    public fun onNullCheckboxClick()
    public fun onSaveClick()
}
