package ru.bartwell.kick.module.runner.core.renderer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import ru.bartwell.kick.module.runner.core.data.RunnerRenderer

public class JsonRunnerRenderer : RunnerRenderer<String?> {
    private var text: String = "null"

    override fun setResult(result: String?) {
        text = result ?: "null"
    }

    @Composable
    override fun RenderContent(modifier: Modifier) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(text = text, fontFamily = FontFamily.Monospace)
        }
    }
}
