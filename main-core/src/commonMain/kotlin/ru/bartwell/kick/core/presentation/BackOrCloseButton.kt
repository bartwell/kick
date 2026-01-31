package ru.bartwell.kick.core.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable

@Composable
public fun BackOrCloseButton(
    onBack: () -> Unit,
) {
    val uiEnvironment = LocalAppUiEnvironment.current
    val canGoBack = uiEnvironment.canNavigateBack
    val action = if (canGoBack) onBack else uiEnvironment.screenCloser
    val icon = if (canGoBack) Icons.AutoMirrored.Outlined.ArrowBack else Icons.Outlined.Close
    val description = if (canGoBack) "Back" else "Close"

    IconButton(onClick = action) {
        Icon(imageVector = icon, contentDescription = description)
    }
}
