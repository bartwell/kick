package ru.bartwell.kick.module.runner.core.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

public interface RunnerRenderer<T> {
    public fun setResult(result: T)

    @Composable
    public fun getContent(modifier: Modifier = Modifier)
}
