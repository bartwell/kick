package ru.bartwell.kick.runtime.feature.table.presentation

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable

@Composable
internal actual fun screenCloser(): () -> Unit {
    val activity = LocalActivity.current
    return { activity?.finish() }
}
