package ru.bartwell.kick.module.ktor3.feature.detail.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.bartwell.kick.core.data.platformContext
import ru.bartwell.kick.module.ktor3.core.persist.RequestEntity
import ru.bartwell.kick.module.ktor3.feature.detail.extension.formatDuration
import ru.bartwell.kick.module.ktor3.feature.detail.extension.formatFileSize
import ru.bartwell.kick.module.ktor3.feature.detail.extension.formatJson
import ru.bartwell.kick.module.ktor3.feature.detail.extension.formatTimestamp
import ru.bartwell.kick.module.ktor3.feature.detail.extension.getPath
import ru.bartwell.kick.module.ktor3.feature.detail.extension.hasJsonContentType
import ru.bartwell.kick.module.ktor3.feature.list.data.Header

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RequestDetailsContent(component: RequestDetailsComponent, modifier: Modifier = Modifier) {
    val state by component.model.subscribeAsState()
    val context = platformContext()
    val tabTitles = listOf("Overview", "Request", "Response")

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                val title = state.request?.let { it.method.name + " " + it.url.getPath() } ?: "Request details"
                Text(title, maxLines = 1)
            },
            navigationIcon = {
                IconButton(onClick = component::onBackPressed) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { component.onCopyClick(context) }, modifier = Modifier.testTag("copy_all")) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy all details")
                }
            }
        )
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }
        val request = state.request
        if (request == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Request not found")
            }
            return
        }
        TabRow(selectedTabIndex = state.selectedTab) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = state.selectedTab == index,
                    onClick = { component.onTabSelected(index) },
                    text = { Text(title) }
                )
            }
        }
        when (state.selectedTab) {
            0 -> OverviewTab(request)
            1 -> RequestTab(state.requestHeaders, state.requestBody)
            2 -> ResponseTab(state.responseHeaders, state.responseBody)
        }
    }
}

@Composable
private fun OverviewTab(request: RequestEntity) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(text = "URL: " + request.url)
        Text(text = "Method: " + request.method.name)
        Text(text = "Status: " + (request.statusCode ?: "-"))
        Text(text = "SSL: " + if (request.isSecure) "Yes" else "No")
        Text(text = "Request time: " + request.formatTimestamp())
        Text(text = "Duration: " + request.durationMs.formatDuration())
        Text(text = "Response size: " + (request.responseSizeBytes?.formatFileSize() ?: "-"))
        request.error?.let { error ->
            Text(text = "Error: $error", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun RequestTab(headers: List<Header>, body: String?) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text("Headers:", style = MaterialTheme.typography.titleMedium)
        if (headers.isEmpty()) {
            Text(text = "No headers")
        } else {
            headers.forEach {
                Text(text = it.key + ": " + it.value)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Body:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.small,
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = body.formatBody(headers.hasJsonContentType()),
                fontFamily = FontFamily.Monospace,
            )
        }
    }
}

@Composable
private fun ResponseTab(headers: List<Header>, body: String?) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text("Headers:", style = MaterialTheme.typography.titleMedium)
        if (headers.isEmpty()) {
            Text("No headers")
        } else {
            headers.forEach {
                Text("${it.key}: ${it.value}")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Body:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = body.formatBody(headers.hasJsonContentType()),
                fontFamily = FontFamily.Monospace,
            )
        }
    }
}

private fun String?.formatBody(hasJsonContentType: Boolean) = if (isNullOrBlank()) {
    "No body"
} else if (hasJsonContentType) {
    formatJson()
} else {
    this
}
