package ru.bartwell.kick.module.runner.feature.result.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.bartwell.kick.core.presentation.BackOrCloseButton
import ru.bartwell.kick.core.presentation.ErrorBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RunnerResultContent(
    component: RunnerResultComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.model.subscribeAsState()

    Column(modifier = modifier) {
        TopAppBar(
            title = { Text("Result") },
            navigationIcon = {
                BackOrCloseButton(onBack = component::onBackPressed)
            },
        )

        ErrorBox(error = state.error, modifier = Modifier.fillMaxSize()) {
            val renderer = state.renderer
            if (renderer != null) {
                renderer.getContent(modifier = Modifier.fillMaxSize())
            } else {
                Text("No result available")
            }
        }
    }
}
