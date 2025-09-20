package ru.bartwell.kick.module.layout.feature.properties.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component

internal interface LayoutPropertiesComponent : Component {
    val model: Value<LayoutPropertiesState>
    fun onBackPressed()
}
