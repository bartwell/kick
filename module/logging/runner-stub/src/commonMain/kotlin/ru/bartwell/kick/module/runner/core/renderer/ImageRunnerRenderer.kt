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
import ru.bartwell.kick.module.runner.core.data.Content
import ru.bartwell.kick.module.runner.core.data.PlatformImage

public class ImageRunnerRenderer : ru.bartwell.kick.module.runner.core.data.RunnerRenderer<PlatformImage?> {
    private var image by mutableStateOf<PlatformImage?>(null)

    override fun setResult(result: PlatformImage?) {
        image = result
    }

    @Composable
    override fun RenderContent(modifier: Modifier) {
        val current = image
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (current != null) {
                current.Content(modifier = Modifier.fillMaxSize().padding(16.dp))
            } else {
                Text("No image", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
