package ru.bartwell.kick.module.runner.core.renderer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.bartwell.kick.module.runner.core.data.RunnerRenderer

public class ObjectRunnerRenderer : RunnerRenderer<Any?> {
    private var value: Any? by mutableStateOf(null)

    override fun setResult(result: Any?) {
        value = result
    }

    @Composable
    override fun getContent(modifier: Modifier) {
        val text = value?.toString() ?: "null"
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
