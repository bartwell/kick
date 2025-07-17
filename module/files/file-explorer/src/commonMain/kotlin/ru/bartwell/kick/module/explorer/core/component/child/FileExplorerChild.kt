package ru.bartwell.kick.module.explorer.core.component.child

import ru.bartwell.kick.core.component.Child
import ru.bartwell.kick.module.explorer.feature.list.presentation.FileExplorerComponent

internal data class FileExplorerChild(
    override val component: FileExplorerComponent,
) : Child<FileExplorerComponent>
