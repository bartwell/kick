package ru.bartwell.kick.module.overlay.core.overlay

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.bartwell.kick.module.overlay.core.store.OverlayStore

@Composable
internal fun OverlayWindow(onCloseClick: () -> Unit) {
    val lines by OverlayStore.items.collectAsState()

    Surface(
        tonalElevation = 6.dp,
        shadowElevation = 12.dp,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline),
    ) {
        Box {
            Column(
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .padding(vertical = 2.dp)
                    .padding(end = 16.dp)
                    .verticalScroll(rememberScrollState())

            ) {
                Spacer(Modifier.height(2.dp))
                lines.forEach { (k, v) ->
                    Text(
                        text = "$k: $v",
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(2.dp))
                }
            }

            Icon(
                modifier = Modifier
                    .padding(2.dp)
                    .size(20.dp)
                    .align(Alignment.TopEnd)
                    .clip(CircleShape)
                    .clickable(onClick = onCloseClick)
                    .padding(2.dp),
                imageVector = Icons.Default.Close,
                contentDescription = null
            )
        }
    }
}
