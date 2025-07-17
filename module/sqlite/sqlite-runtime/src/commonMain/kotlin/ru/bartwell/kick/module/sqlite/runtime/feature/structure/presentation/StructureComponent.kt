package ru.bartwell.kick.feature.structure.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component

public interface StructureComponent : Component {
    public val model: Value<StructureState>

    public fun onBackPressed()
}
