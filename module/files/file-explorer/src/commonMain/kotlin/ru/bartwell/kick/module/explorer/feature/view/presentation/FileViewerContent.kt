package ru.bartwell.kick.module.explorer.feature.view.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.bartwell.kick.core.data.platformContext
import ru.bartwell.kick.core.presentation.ErrorAlert

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FileViewerContent(component: FileViewerComponent, modifier: Modifier = Modifier) {
    val state by component.model.subscribeAsState()
    val context = platformContext()
    LaunchedEffect(Unit) { component.init(context) }

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(state.fileName) },
            navigationIcon = {
                IconButton(onClick = component::onBackClick) {
                    Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null)
                }
            }
        )
        Text(
            text = state.text,
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        )
    }

    state.error?.let { error ->
        ErrorAlert(message = error, onDismiss = component::onErrorAlertDismiss)
    }
}
