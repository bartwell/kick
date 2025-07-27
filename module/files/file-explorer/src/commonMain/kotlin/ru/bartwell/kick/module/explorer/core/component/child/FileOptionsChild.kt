package ru.bartwell.kick.module.explorer.core.component.child

import ru.bartwell.kick.core.component.Child
import ru.bartwell.kick.module.explorer.feature.options.presentation.FileOptionsComponent

internal data class FileOptionsChild(
    override val component: FileOptionsComponent,
) : Child<FileOptionsComponent>
