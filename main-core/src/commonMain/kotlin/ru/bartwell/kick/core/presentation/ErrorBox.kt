package ru.bartwell.kick.core.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
public fun ErrorBox(
    error: String?,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier) {
        if (error == null) {
            content()
        } else {
            Text(
                modifier = Modifier.align(Alignment.Center)
                    .padding(16.dp),
                text = error,
            )
        }
    }
}
