package ru.bartwell.kick.module.sqlite.runtime.feature.structure.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component

public interface StructureComponent : Component {
    public val model: Value<StructureState>

    public fun onBackPressed()
}
