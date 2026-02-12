package ru.bartwell.kick.module.firebase.analytics.core.overlay

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.bartwell.kick.module.firebase.analytics.core.util.FirebaseFloatingWindowState

@Composable
internal fun FirebaseFloatingWindowContent(onClick: () -> Unit) {
    val lines by FirebaseFloatingWindowState.lines.collectAsState()
    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 12.dp,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 6.dp, horizontal = 12.dp)
                .widthIn(min = 150.dp, max = 280.dp),
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                if (lines.isEmpty()) {
                    Text(
                        text = "No events",
                        style = MaterialTheme.typography.bodySmall,
                    )
                } else {
                    lines.forEach { line ->
                        Text(
                            text = line,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}
