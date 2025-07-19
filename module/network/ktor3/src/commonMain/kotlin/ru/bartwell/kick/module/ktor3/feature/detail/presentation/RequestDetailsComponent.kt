package ru.bartwell.kick.module.ktor3.feature.detail.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component
import ru.bartwell.kick.core.data.PlatformContext

internal interface RequestDetailsComponent : Component {
    val model: Value<RequestDetailsState>
    fun onBackPressed()
    fun onTabSelected(index: Int)
    fun onCopyClick(context: PlatformContext)
}
