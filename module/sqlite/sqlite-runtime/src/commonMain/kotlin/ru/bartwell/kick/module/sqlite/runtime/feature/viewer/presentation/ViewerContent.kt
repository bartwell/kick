package ru.bartwell.kick.module.sqlite.runtime.feature.viewer.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.bartwell.kick.core.presentation.ErrorAlert
import ru.bartwell.kick.core.presentation.ErrorBox
import ru.bartwell.kick.module.sqlite.runtime.feature.viewer.presentation.table.Table

@Composable
internal fun ViewerContent(
    component: ViewerComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.model.subscribeAsState()
    Column(modifier = modifier) {
        Toolbar(
            state = state,
            onBackPressed = component::onBackPressed,
            onCancelDelete = component::onCancelDeleteClick,
            onConfirmDelete = component::onConfirmDeleteClick,
            onStructureClick = component::onStructureClick,
            onInsertClick = component::onInsertClick,
            onDeleteClick = component::onDeleteClick,
        )
        ErrorBox(
            modifier = Modifier.fillMaxSize(),
            error = state.loadError,
        ) {
            Table(
                columns = state.columns,
                rows = state.rows,
                isInSelectionMode = state.isDeleteMode,
                selectedRows = state.selectedRows,
                onCellClick = component::onCellClick,
                onRowSelected = component::onRowSelected,
            )
        }
    }
    state.deleteError?.let { error ->
        ErrorAlert(message = error, onDismiss = component::onAlertDismiss)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Toolbar(
    state: ViewerState,
    onBackPressed: () -> Unit,
    onCancelDelete: () -> Unit,
    onConfirmDelete: () -> Unit,
    onStructureClick: () -> Unit,
    onInsertClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    TopAppBar(
        title = { Text(text = if (state.isDeleteMode) "Delete" else state.table) },
        navigationIcon = {
            if (state.isDeleteMode) {
                IconButton(onClick = onCancelDelete) {
                    Icon(imageVector = Icons.Outlined.Close, contentDescription = "Cancel")
                }
            } else {
                IconButton(onClick = onBackPressed) {
                    Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = {
            if (state.isDeleteMode) {
                IconButton(onClick = onConfirmDelete) {
                    Icon(imageVector = Icons.Default.Done, contentDescription = "Delete selected")
                }
            } else {
                IconButton(onClick = { isMenuExpanded = true }) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menu")
                }
                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Structure") },
                        onClick = {
                            isMenuExpanded = false
                            onStructureClick()
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Insert row") },
                        onClick = {
                            isMenuExpanded = false
                            onInsertClick()
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            isMenuExpanded = false
                            onDeleteClick()
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Outlined.Delete, contentDescription = null)
                        }
                    )
                }
            }
        }
    )
}
