package ru.bartwell.kick.runtime.feature.list.presentation

import androidx.compose.runtime.Composable
import ru.bartwell.kick.runtime.core.util.IosSceneController

@Composable
internal actual fun screenCloser(): () -> Unit {
    return { IosSceneController.dismiss() }
}
