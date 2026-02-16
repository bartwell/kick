package ru.bartwell.kick.module.runner.core.renderer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.bartwell.kick.module.runner.core.data.RunnerRenderer

/**
 * Renderer that shows any object via its `toString()` representation.
 */
public class ObjectRunnerRenderer : RunnerRenderer<Any?> {
    private var value: Any? by mutableStateOf(null)

    override fun setResult(result: Any?) {
        value = result
    }

    @Composable
    override fun RenderContent(modifier: Modifier) {
        val scrollState = rememberScrollState()
        val text = value?.toString() ?: "null"
        Box(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
