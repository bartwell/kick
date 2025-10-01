package ru.bartwell.kick.module.overlay.core.overlay

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.bartwell.kick.module.overlay.core.store.OverlayStore

@Composable
internal fun OverlayWindow(onCloseClick: () -> Unit, measureFull: Boolean = false) {
    val lines by OverlayStore.items.collectAsState()

    val shape = RectangleShape
    Box(
        modifier = Modifier
            .shadow(elevation = 12.dp, shape = shape, clip = true)
            .background(color = MaterialTheme.colorScheme.surface, shape = shape)
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), shape = shape)
            .clip(shape)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .padding(vertical = 2.dp)
                .padding(end = 16.dp)
        ) {
            Spacer(Modifier.height(2.dp))
            lines.forEach { (k, v) ->
                val (ml, sw, of) = if (measureFull) {
                    Triple(Int.MAX_VALUE, false, TextOverflow.Clip)
                } else {
                    Triple(1, false, TextOverflow.Ellipsis)
                }
                Text(
                    text = "$k: $v",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Clip,
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
