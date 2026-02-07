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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import ru.bartwell.kick.module.runner.core.data.RunnerRenderer

/**
 * Built-in renderer that pretty-prints a JSON string (nullable).
 */
@OptIn(ExperimentalSerializationApi::class)
public class JsonRunnerRenderer : RunnerRenderer<String?> {

    private val json: Json = Json {
        prettyPrint = true
        prettyPrintIndent = "  "
        isLenient = true
        ignoreUnknownKeys = true
    }

    private var formatted: String? by mutableStateOf(null)

    override fun setResult(result: String?) {
        formatted = formatJson(result)
    }

    @Composable
    override fun getContent(modifier: Modifier) {
        val scrollState = rememberScrollState()
        val text = formatted ?: "null"
        Box(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Monospace,
            )
        }
    }

    private fun formatJson(raw: String?): String {
        if (raw.isNullOrBlank()) return "null"
        return try {
            val element: JsonElement = json.parseToJsonElement(raw)
            json.encodeToString(JsonElement.serializer(), element)
        } catch (_: Exception) {
            // Fall back to the original string if parsing fails.
            raw
        }
    }
}
