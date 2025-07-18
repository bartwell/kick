package ru.bartwell.kick.runtime.feature.stub.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StubContent(
    component: StubComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.model.subscribeAsState()

    Column(modifier = modifier) {
        TopAppBar(
            title = { Text("No module") },
            navigationIcon = {
                IconButton(onClick = component::onBackPressed) {
                    Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                }
            },
        )
        val moduleName = state.moduleDescription.title
        Text(
            modifier = Modifier.padding(16.dp),
            text = "The $moduleName module is currently disabled. Please verify that:\n" +
                "\n" +
                "- The module has been added to your Gradle configuration.\n" +
                "\n" +
                "- You are using the implementation module rather than the stub module.\n" +
                "\n" +
                "- The module is initialized inside the Kick.init(context) { … } block."
        )
    }
}
