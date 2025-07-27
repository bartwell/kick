package ru.bartwell.kick.module.explorer.feature.options.presentation

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.bartwell.kick.core.data.platformContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FileOptionsContent(component: FileOptionsComponent) {
    val state by component.model.subscribeAsState()
    val context = platformContext()

    if (state.isSheetVisible) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = component::onDismiss,
            sheetState = sheetState
        ) {
            ListItem(
                modifier = Modifier.clickable { component.onDownload(context) },
                headlineContent = { Text("Download") }
            )
            ListItem(
                modifier = Modifier.clickable(component::onViewAsText),
                headlineContent = { Text("View as text") }
            )
        }
    }

    state.alertMessage?.let { message ->
        AlertDialog(
            onDismissRequest = component::onAlertDismiss,
            confirmButton = {
                TextButton(onClick = component::onAlertDismiss) { Text("OK") }
            },
            title = { Text("Export complete") },
            text = { Text(message) }
        )
    }
}
