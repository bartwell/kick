package ru.bartwell.kick.module.configuration.core.component.child

import ru.bartwell.kick.core.component.Child
import ru.bartwell.kick.module.configuration.feature.presentation.ConfigurationComponent

internal data class ConfigurationChild(
    override val component: ConfigurationComponent,
) : Child<ConfigurationComponent>
