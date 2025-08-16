package ru.bartwell.kick.module.layout.feature.hierarchy.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component
import ru.bartwell.kick.module.layout.core.data.LayoutNodeId

internal interface LayoutHierarchyComponent : Component {
    val model: Value<LayoutHierarchyState>
    fun onNodeSelected(id: LayoutNodeId)
}
