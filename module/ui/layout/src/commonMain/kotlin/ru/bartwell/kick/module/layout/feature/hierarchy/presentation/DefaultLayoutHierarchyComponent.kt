package ru.bartwell.kick.module.layout.feature.hierarchy.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ru.bartwell.kick.module.layout.core.data.LayoutNodeId
import ru.bartwell.kick.module.layout.core.introspector.LayoutRepository

internal class DefaultLayoutHierarchyComponent(
    componentContext: ComponentContext,
    private val repository: LayoutRepository,
    private val onNodeSelectedCallback: (LayoutNodeId) -> Unit,
) : LayoutHierarchyComponent, ComponentContext by componentContext {

    private val _model = MutableValue(LayoutHierarchyState())
    override val model: Value<LayoutHierarchyState> = _model

    init {
        coroutineScope().launch {
            _model.value = LayoutHierarchyState(repository.captureHierarchy())
        }
    }

    override fun onNodeSelected(id: LayoutNodeId) = onNodeSelectedCallback(id)
}
