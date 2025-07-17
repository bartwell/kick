package ru.bartwell.kick.runtime.feature.table.presentation

import androidx.compose.runtime.Composable
import ru.bartwell.kick.runtime.core.util.IosSceneController

@Composable
internal actual fun screenCloser(): () -> Unit {
    return { IosSceneController.dismiss() }
}
