package ru.bartwell.kick.module.firebase.analytics.feature.properties.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.bartwell.kick.core.presentation.ErrorBox
import ru.bartwell.kick.core.util.DateUtils
import ru.bartwell.kick.module.firebase.analytics.core.data.UserProperty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FirebaseAnalyticsPropertiesContent(
    component: FirebaseAnalyticsPropertiesComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.model.subscribeAsState()

    Column(modifier = modifier) {
        TopAppBar(
            title = { Text("Firebase Analytics Properties") },
            navigationIcon = {
                IconButton(onClick = component::onBackPressed) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                }
            }
        )
        ErrorBox(error = state.error, modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
            ) {
                if (state.properties.isEmpty()) {
                    item {
                        Text(
                            text = "No properties",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                } else {
                    items(state.properties) { property ->
                        PropertyItem(property)
                    }
                }
            }
        }
    }
}

@Composable
private fun PropertyItem(property: UserProperty) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Text(
                text = property.name,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = property.value,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = DateUtils.formatLogTime(property.timestamp),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
