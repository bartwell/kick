package ru.bartwell.kick.module.layout.feature.properties.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ru.bartwell.kick.module.layout.core.data.LayoutNodeId
import ru.bartwell.kick.module.layout.core.introspector.LayoutRepository

internal class DefaultLayoutPropertiesComponent(
    componentContext: ComponentContext,
    private val nodeId: LayoutNodeId,
    private val repository: LayoutRepository,
    private val onFinished: () -> Unit,
) : LayoutPropertiesComponent, ComponentContext by componentContext {

    private val _model = MutableValue(LayoutPropertiesState())
    override val model: Value<LayoutPropertiesState> = _model

    init {
        load()
    }

    override fun onBackPressed() = onFinished()

    private fun load() {
        coroutineScope().launch {
            // 1-я попытка: использовать текущие карты интроспектора
            var props = repository.propertiesOf(nodeId)

            // Если пусто — восстановимся: захватим иерархию заново и повторим запрос.
            if (props.isEmpty()) {
                repository.captureHierarchy()
                props = repository.propertiesOf(nodeId)
            }

            _model.value = LayoutPropertiesState(props)
        }
    }
}
